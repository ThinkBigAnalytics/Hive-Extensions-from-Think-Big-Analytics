/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package thinkbig.hive.serde;

import java.beans.*;
import java.lang.reflect.*;
import java.util.*;

import org.apache.hadoop.hive.serde2.objectinspector.*;
import org.apache.hadoop.hive.serde2.objectinspector.ReflectionStructObjectInspector.MyField;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorUtils;
import org.apache.hadoop.util.ReflectionUtils;

/**
 * BeansStructObjectInspector works on struct data that is stored as a native Java object. It will drill down into the Java class
 * to find bean info/beans methods (getter and setter pairs) and construct ObjectInspectors for the fields, if they are not
 * specified.
 * 
 * Always use the ObjectInspectorFactory to create new ObjectInspector objects, instead of directly creating an instance of this
 * class.
 * 
 */
public class BeansStructObjectInspector extends SettableStructObjectInspector {

    /**
     * MyField.
     * 
     */
    public static class MyField implements StructField {
        protected PropertyDescriptor propertyDescriptor;
        protected ObjectInspector fieldObjectInspector;

        public MyField(PropertyDescriptor propertyDescriptor, ObjectInspector fieldObjectInspector) {
            this.propertyDescriptor = propertyDescriptor;
            this.fieldObjectInspector = fieldObjectInspector;
        }

        public String getFieldName() {
            return propertyDescriptor.getName().toLowerCase();
        }

        public ObjectInspector getFieldObjectInspector() {
            return fieldObjectInspector;
        }

        @Override
        public String toString() {
            return propertyDescriptor.toString();
        }
    }

    Class<?> objectClass;
    List<MyField> fields;

    public Category getCategory() {
        return Category.STRUCT;
    }

    public String getTypeName() {
        return objectClass.getName();
    }

    /**
     * This method is only intended to be used by the Utilities class in this package. This creates an uninitialized
     * ObjectInspector so the Utilities class can put it into a cache before it initializes when it might look up the cache for
     * member fields that might be of the same type (e.g. recursive type like linked list and trees).
     */
    // XXX temporarily not cached
    public BeansStructObjectInspector() {
    }

    public static ObjectInspector getBeansObjectInspector(Type t) {
        if (t instanceof GenericArrayType) {
            GenericArrayType at = (GenericArrayType) t;
            return ObjectInspectorFactory.getStandardListObjectInspector(getBeansObjectInspector(at.getGenericComponentType()));
        }

        if (t instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) t;
            // List?
            if (List.class.isAssignableFrom((Class<?>) pt.getRawType())) {
                return ObjectInspectorFactory
                        .getStandardListObjectInspector(getBeansObjectInspector(pt.getActualTypeArguments()[0]));
            }
            // Map?
            if (Map.class.isAssignableFrom((Class<?>) pt.getRawType())) {
                return ObjectInspectorFactory.getStandardMapObjectInspector(
                        getBeansObjectInspector(pt.getActualTypeArguments()[0]),
                        getBeansObjectInspector(pt.getActualTypeArguments()[1]));
            }
            // Otherwise convert t to RawType so we will fall into the following if
            // block.
            t = pt.getRawType();
        }

        // Must be a class.
        if (!(t instanceof Class)) {
            throw new RuntimeException(ObjectInspectorFactory.class.getName() + " internal error:" + t);
        }
        Class<?> c = (Class<?>) t;

        // Java Primitive Type?
        if (PrimitiveObjectInspectorUtils.isPrimitiveJavaType(c)) {
            return PrimitiveObjectInspectorFactory.getPrimitiveJavaObjectInspector(PrimitiveObjectInspectorUtils
                    .getTypeEntryFromPrimitiveJavaType(c).primitiveCategory);
        }

        // Java Primitive Class?
        if (PrimitiveObjectInspectorUtils.isPrimitiveJavaClass(c)) {
            return PrimitiveObjectInspectorFactory.getPrimitiveJavaObjectInspector(PrimitiveObjectInspectorUtils
                    .getTypeEntryFromPrimitiveJavaClass(c).primitiveCategory);
        }

        // Primitive Writable class?
        if (PrimitiveObjectInspectorUtils.isPrimitiveWritableClass(c)) {
            return PrimitiveObjectInspectorFactory.getPrimitiveWritableObjectInspector(PrimitiveObjectInspectorUtils
                    .getTypeEntryFromPrimitiveWritableClass(c).primitiveCategory);
        }

        // Must be struct because List and Map need to be ParameterizedType
        assert (!List.class.isAssignableFrom(c));
        assert (!Map.class.isAssignableFrom(c));

        BeansStructObjectInspector oi = new BeansStructObjectInspector();

        BeanInfo beanInfo;
        try {
            beanInfo = getBeanInfo(c);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            ArrayList<ObjectInspector> structFieldObjectInspectors = new ArrayList<ObjectInspector>(propertyDescriptors.length);

            for (int i = 0; i < propertyDescriptors.length; i++) {
                // we need the generic return type so we know the right type for lists or maps
                structFieldObjectInspectors.add(getBeansObjectInspector(propertyDescriptors[i].getReadMethod()
                        .getGenericReturnType()));
            }
            oi.init(c, structFieldObjectInspectors);
            return oi;
        }
        catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }

    private static BeanInfo getBeanInfo(Class<?> c) throws IntrospectionException {
        BeanInfo beanInfo;
        if (!c.isInterface())
            beanInfo = Introspector.getBeanInfo(c, Object.class);
        else
            beanInfo = Introspector.getBeanInfo(c);
        return beanInfo;
    }

    /**
     * This method is only intended to be used by Utilities class in this package. The reason that this method is not recursive by
     * itself is because we want to allow recursive types.
     */
    void init(Class<?> objectClass, List<ObjectInspector> structFieldObjectInspectors) {
        assert (!List.class.isAssignableFrom(objectClass));
        assert (!Map.class.isAssignableFrom(objectClass));
        this.objectClass = objectClass;

        try {
            BeanInfo beanInfo = getBeanInfo(objectClass);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            fields = new ArrayList<MyField>(structFieldObjectInspectors.size());
            int used = 0;
            for (int i = 0; i < propertyDescriptors.length; i++) {
                fields.add(new MyField(propertyDescriptors[i], structFieldObjectInspectors.get(used++)));
            }
        }
        catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
        assert (fields.size() == structFieldObjectInspectors.size());
    }

    // ThriftStructObjectInspector will override and ignore __isset fields.
    public boolean shouldIgnoreField(String name) {
        return false;
    }

    // Without Data
    @Override
    public StructField getStructFieldRef(String fieldName) {
        return ObjectInspectorUtils.getStandardStructFieldRef(fieldName, fields);
    }

    @Override
    public List<? extends StructField> getAllStructFieldRefs() {
        return fields;
    }

    // With Data
    @Override
    public Object getStructFieldData(Object data, StructField fieldRef) {
        if (data == null) {
            return null;
        }
        if (!(fieldRef instanceof MyField)) {
            throw new RuntimeException("fieldRef has to be of MyField");
        }
        MyField f = (MyField) fieldRef;
        try {
            Object r = f.propertyDescriptor.getReadMethod().invoke(data);
            return r;
        }
        catch (Exception e) {
            throw new RuntimeException("cannot get field " + f.propertyDescriptor + " from " + data.getClass() + " " + data, e);
        }
    }

    @Override
    public List<Object> getStructFieldsDataAsList(Object data) {
        if (data == null) {
            return null;
        }
        try {
            ArrayList<Object> result = new ArrayList<Object>(fields.size());
            for (int i = 0; i < fields.size(); i++) {
                result.add(fields.get(i).propertyDescriptor.getReadMethod().invoke(data));
            }
            return result;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object create() {
        return ReflectionUtils.newInstance(objectClass, null);
    }

    @Override
    public Object setStructFieldData(Object struct, StructField field, Object fieldValue) {
        MyField myField = (MyField) field;
        try {
            myField.propertyDescriptor.getWriteMethod().invoke(struct, fieldValue);
        }
        catch (Exception e) {
            throw new RuntimeException("cannot set field " + myField.propertyDescriptor + " of " + struct.getClass() + " "
                    + struct, e);
        }
        return struct;
    }

}

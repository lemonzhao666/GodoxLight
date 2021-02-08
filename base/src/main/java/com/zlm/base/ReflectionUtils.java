package com.zlm.base;

/*
 * Copyright (C) 2015 Johnny Shieh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionUtils {

    public static Class<?>[] getClassTypes(Object[] args) {
        if(null == args || args.length == 0) {
            return null;
        }
        Class<?>[] argClasses = new Class<?>[args.length];
        for(int i = 0; i < args.length; i ++){
            argClasses[i] = args[i].getClass();
        }
        return argClasses;
    }

    /**
     * 根据类名和构造函数的参数新建一个对象实例，只能通过public的构造函数新建实例。
     * @param className 类名
     * @param args      构造函数的参数
     * @param argClasses 构造函数的参数的class type
     * @return 该类的对象实例
     */
    public static Object newInstance(String className, Object[] args, Class<?>[] argClasses) {
        Object instance = null;
        try {
            Class classObj = Class.forName(className);
            Constructor cons = classObj.getConstructor(argClasses);
            instance = cons.newInstance(args);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return instance;
    }

    /**
     * 获得类的静态属性值，不管是public或者private的都可以。
     * @param className 类名
     * @param fieldName 属性名
     * @return 该类的静态属性值
     */
    public static Object getStaticField(String className, String fieldName) {
        Object fieldValue = null;
        try {
            Class classObj = Class.forName(className);
            Field field = classObj.getDeclaredField(fieldName);
            field.setAccessible(true);
            fieldValue = field.get(null); // note: Field.get(Object obj), If this field is static, the object argument is ignored.
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return fieldValue;
    }

    /**
     * 修改类的静态属性值，不管是public或者private的都可以。
     * @param className 类名
     * @param fieldName 属性名
     * @param value     修改后的属性值
     * @return true，如果修改成功;false，如果修改失败
     */
    public static boolean setStaticField(String className, String fieldName, Object value) {
        boolean result = false;
        try {
            Class classObj = Class.forName(className);
            Field field = classObj.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(null, value);
            result = true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获得对象的属性值，不管是public或者private的都可以，
     * 但是该方法只能获得在该对象的类中所定义的成员变量的值，不能获得其父类的成员变量的值。
     * @param owner     对象
     * @param fieldName 对象所属类的属性名
     * @return 对象所属类中的属性值
     */
    public static Object getField(Object owner, String fieldName) {
        Object fieldValue = null;
        try {
            Class classObj = owner.getClass();
            Field field = classObj.getDeclaredField(fieldName);
            field.setAccessible(true);
            fieldValue = field.get(owner);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return fieldValue;
    }

    /**
     * 获得对象的属性值（属性是对象的父类中定义的），不管是public或者private的都可以。
     * @param owner     对象
     * @param className 对象所属类的父类的类名
     * @param fieldName 对象所属类的父类的属性名
     * @return 对象所属类的父类的属性值
     */
    public static Object getField(Object owner, String className, String fieldName) {
        Object fieldValue = null;
        try {
            Class classObj = Class.forName(className);
            Field field = classObj.getDeclaredField(fieldName);
            field.setAccessible(true);
            fieldValue = field.get(owner);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return fieldValue;
    }

    /**
     * 修改对象的属性值，不管是public或者private的都可以，
     * 但是该方法只能修改在该对象的类中所定义的成员变量的值，不能修改其父类的成员变量的值。
     * @param owner     对象
     * @param fieldName 对象所属类的属性名
     * @param value     修改后的属性值
     * @return true，如果修改成功;false，如果修改失败
     */
    public static boolean setField(Object owner, String fieldName, Object value) {
        boolean result = false;
        try {
            Class classObj = owner.getClass();
            Field field = classObj.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(owner, value);
            result = true;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 修改对象的属性值（属性是对象的父类中定义的），不管是public或者private的都可以。
     * @param owner     对象
     * @param className 对象所属类的父类的类名
     * @param fieldName 对象所属类的父类的属性名
     * @param value     修改后的属性值
     * @return true，如果修改成功;false，如果修改失败
     */
    public static boolean setField(Object owner, String className, String fieldName, Object value) {
        boolean result = false;
        try {
            Class classObj = Class.forName(className);
            Field field = classObj.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(owner, value);
            result = true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 调用类的静态方法，不管是public或者private的都可以。
     * @param className     类名
     * @param methodName    静态方法名
     * @param args          方法的参数
     * @param argClasses    方法的参数的class type
     * @return 静态方法的返回值
     */
    public static Object invokeStaticMethod(String className, String methodName, Object[] args, Class<?>[] argClasses) {
        Object result = null;
        try {
            Class classObj = Class.forName(className);
            Method method = classObj.getDeclaredMethod(methodName, argClasses);
            method.setAccessible(true);
            result = method.invoke(null, args);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 调用对象的方法，不管是public或者private的都可以。
     * 但是该方法只能调用对象所属类中定义的方法，不能调用其父类中定义的方法。
     * @param owner         对象
     * @param methodName    对象所属类的方法名
     * @param args          方法的参数
     * @param argClasses    方法的参数的class type
     * @return 方法的返回值
     */
    public static Object invokeMethod(Object owner, String methodName, Object[] args, Class<?>[] argClasses) {
        Object result = null;
        try {
            Class classObj = owner.getClass();
            Method method = classObj.getDeclaredMethod(methodName, argClasses);
            method.setAccessible(true);
            result = method.invoke(owner, args);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return  result;
    }

    /**
     * 调用对象的方法（方法是在对象所属类的父类中定义的），不管是public或者private的都可以。
     * 如果这个方法在子类中重写的话，实际上还是调用子类的重写方法
     * @param owner         对象
     * @param className     对象所属类的父类的类名
     * @param methodName    对象所属类的父类的方法名
     * @param args          方法参数
     * @param argClasses    方法的参数的class type
     * @return 方法的返回值
     */
    public static Object invokeMethod(Object owner, String className, String methodName, Object[] args, Class<?>[] argClasses) {
        Object result = null;
        try {
            Class classObj = Class.forName(className);
            Method method = classObj.getDeclaredMethod(methodName, argClasses);
            method.setAccessible(true);
            result = method.invoke(owner, args);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return  result;
    }

    /**
     * 新建一个类型的数组
     * @param className 数组的类型的类名
     * @param len       数组的大小
     * @return 数组
     */
    public static Object newArrayInstance (String className, int len) {
        Object result = null;
        try {
            Class classObj = Class.forName(className);
            result = Array.newInstance(classObj, len);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获得数组中的一个元素
     * @param array 数组
     * @param index 元素在数组中的位置
     * @return 数组中的这个元素
     */
    public static Object getObjectInArray(Object array, int index) {
        return Array.get(array, index);
    }

    /**
     * 修改数组中的一个元素的值
     * @param array 数组
     * @param index 元素在数组中的位置
     * @param value 修改后的元素值
     */
    public static void setObjectInArray(Object array, int index, Object value) {
        Array.set(array, index, value);
    }
}

package jvm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DIYClassLoader extends ClassLoader{
    public static void main(String[] args) {
        try {
            Object o = new DIYClassLoader().findClass("Week_01/jvm/src/Hello.xlass").newInstance();
            Class<?> helloClazz = o.getClass();
            Method method = helloClazz.getMethod("hello");
            method.invoke(o);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

   @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            File clazzFile = new File(name);
            FileInputStream input = new FileInputStream(clazzFile);

            byte[] buffer = new byte[Math.toIntExact(clazzFile.length())];
            input.read(buffer);

            byte[] result = new byte[Math.toIntExact(clazzFile.length())];
            for (int i = 0; i < buffer.length; i++) {
                result[i]= (byte) (255-buffer[i]);
            }

            return defineClass(null,result,0,result.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return super.findClass(name);
    }
}

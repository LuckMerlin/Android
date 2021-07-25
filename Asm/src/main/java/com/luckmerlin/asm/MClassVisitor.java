package com.luckmerlin.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import luckmerlin.asm.model.AndroidAttachBaseContextMethodVisitor;

/**
 * Create LuckMerlin
 * Date 18:00 2021/7/23
 * TODO
 */
public class MClassVisitor {
    private final String[] mAttachBaseContextClass=new String[]{"android/app/Activity"
    ,"android.app.Application","android.app.Service"};

    public final boolean isSupportClassName(boolean localClass,String name) {
        if (null==name||name.length()<=0 ||!name.endsWith(".class")||name.startsWith("R\\$")||
                name.equals("R.class")|| name.equals("BuildConfig.class")){
            return false;
        }
        return true;
    }

    public final boolean isClassMatch(boolean localClass,ClassReader reader,String name) {
        boolean matched=isMatched(reader,false,mAttachBaseContextClass);
        return matched;
    }

    private boolean isMatched(ClassReader reader,boolean checkSelf,String... names){
        return null!=reader&&isMatched(checkSelf?new String[]{reader.getClassName(),
                reader.getSuperName()}:new String[]{reader.getSuperName()},names)||
                isMatched(reader.getInterfaces(),names);
    }

    private boolean isMatched(String[] src,String...names){
        if (null==src||src.length<=0||null==names||names.length<=0){
            return false;
        }
        for (String srcChild:src){
            for (String nameChild:names){
                if (null!=srcChild&&null!=nameChild&&srcChild.equals(nameChild)){
                    return true;
                }
            }
        }
        return false;
    }

    public ClassVisitor visit(boolean localClass, String name, ClassReader classReader, ClassWriter classWriter){
        String superName=null!=classReader?classReader.getSuperName():null;
        if (isMatched(mAttachBaseContextClass,superName)){
//            classWriter.getCommonSuperClass();
//            ASMifier.
        }
        System.out.println("了哈是的发生大 "+superName);
        return new ClassVisitor(Opcodes.ASM7,classWriter){
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                if ("attachBaseContext".equals(name)){
                    return new AndroidAttachBaseContextMethodVisitor(cv.visitMethod(access, name, desc, signature, exceptions));
                }
                return super.visitMethod(access, name, desc, signature, exceptions);
            }
        };
    }
}

package com.luckmerlin.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class LifecycleClassVisitor extends ClassVisitor implements Opcodes {
    private String mClassName;

    public LifecycleClassVisitor(ClassVisitor cv) {
        super(Opcodes.ASM6, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
//        System.out.println("LifecycleClassVisitor : visit -----> started ：" + name);
        this.mClassName = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
//        System.out.println("LifecycleClassVisitor : visitMethod : " +access+" "+ name+" desc="+
//                desc+" "+signature+" exceptions="+exceptions);
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        String currentClassName=mClassName;
        if (null!=currentClassName&&null!=name){

        }
        //匹配FragmentActivity
//        if ("android/support/v4/app/FragmentActivity".equals(this.mClassName)) {
//            if ("onCreate".equals(name) ) {
//                //处理onCreate
//                System.out.println("LifecycleClassVisitor : change method ----> " + name);
//                return new LifecycleOnCreateMethodVisitor(mv);
//            } else if ("onDestroy".equals(name)) {
//                //处理onDestroy
//                System.out.println("LifecycleClassVisitor : change method ----> " + name);
//                return new LifecycleOnDestroyMethodVisitor(mv);
//            }
//        }
        return mv;
    }

    @Override
    public void visitEnd() {
//        System.out.println("LifecycleClassVisitor : visit -----> end");
        super.visitEnd();
    }
}

package com.luckmerlin.asm;

import org.objectweb.asm.ClassVisitor;

public class MClassVisitor extends ClassVisitor {

    public MClassVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }
}

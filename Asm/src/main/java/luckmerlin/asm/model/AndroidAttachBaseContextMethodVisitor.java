package luckmerlin.asm.model;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class AndroidAttachBaseContextMethodVisitor extends MethodVisitor implements Opcodes{

    public AndroidAttachBaseContextMethodVisitor(MethodVisitor mv) {
        super(Opcodes.ASM7, mv);
    }

    @Override
    public void visitCode() {
        mv.visitCode();
        Label l0 = new Label();
        Label l1 = new Label();
        Label l2 = new Label();
        mv.visitTryCatchBlock(l0, l1, l2, "java/lang/ClassNotFoundException");
        mv.visitLabel(l0);
        mv.visitLineNumber(12, l0);
        mv.visitLdcInsn("luckmerlin.databinding.AndroidlLifecycle");
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;", false);
        mv.visitInsn(POP);
        Label l3 = new Label();
        mv.visitLabel(l3);
        mv.visitLineNumber(13, l3);
        mv.visitTypeInsn(NEW, "luckmerlin/databinding/AndroidlLifecycle");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "luckmerlin/databinding/AndroidlLifecycle", "<init>", "()V", false);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "luckmerlin/databinding/AndroidlLifecycle", "attachBaseContext", "(Landroid/content/Context;)V", false);
        mv.visitLabel(l1);
        mv.visitLineNumber(16, l1);
        Label l4 = new Label();
        mv.visitJumpInsn(GOTO, l4);
        mv.visitLabel(l2);
        mv.visitLineNumber(14, l2);
        mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/ClassNotFoundException"});
        mv.visitVarInsn(ASTORE, 2);
        mv.visitLabel(l4);
        mv.visitLineNumber(18, l4);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitInsn(RETURN);
        mv.visitEnd();
    }

    @Override
    public void visitInsn(int opcode) {
        super.visitInsn(opcode);
        if (opcode == Opcodes.RETURN) {

        }
    }






}

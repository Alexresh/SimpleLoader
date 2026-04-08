package ru.obabok.mixin;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.service.IClassBytecodeProvider;
import ru.obabok.Main;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class BytecodeProvider implements IClassBytecodeProvider {
    private static IClassBytecodeProvider instance;

    @Override
    public ClassNode getClassNode(String name) throws ClassNotFoundException, IOException {
        return getClassNode(name, true);
    }

    @Override
    public ClassNode getClassNode(String name, boolean runTransformers) throws IOException {
        String path = name.replace('.', '/') + ".class";

        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);

        if (is == null) {
            return null;
        }

        try (is) {
            byte[] bytes = is.readAllBytes();
            ClassReader reader = new ClassReader(bytes);
            ClassNode node = new ClassNode();
            reader.accept(node, ClassReader.EXPAND_FRAMES);
            return node;
        }
    }
    public static IClassBytecodeProvider getInstance(){
        if(instance == null){
            instance = new BytecodeProvider();
        }
        return instance;
    }

}



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
    private final JarFile jar;

    public BytecodeProvider(JarFile jar) {
        this.jar = jar;
    }

    @Override
    public ClassNode getClassNode(String name) throws ClassNotFoundException, IOException {
        return getClassNode(name, true);
    }

//    @Override
//    public ClassNode getClassNode(String name, boolean b) throws ClassNotFoundException, IOException {
//        String path = name.replace('.', '/') + ".class";
//        JarEntry entry = jar.getJarEntry(path);
//
//        if (entry == null) {
//            throw new ClassNotFoundException(name);
//        }
//
//        try (InputStream is = jar.getInputStream(entry)) {
//            byte[] bytes = is.readAllBytes();
//            ClassReader reader = new ClassReader(bytes);
//            ClassNode node = new ClassNode();
//            // Превращаем байты в объектную модель (Node) для Mixin
//            reader.accept(node, ClassReader.EXPAND_FRAMES);
//            return node;
//        }
//    }

    @Override
    public ClassNode getClassNode(String name, boolean runTransformers) throws IOException {
        String path = name.replace('.', '/') + ".class";

        // Пытаемся найти байты через контекстный загрузчик (твой TransformingClassLoader)
        // Он уже умеет бегать по всем JAR (и игры, и модов)
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

}



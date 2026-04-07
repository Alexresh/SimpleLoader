package ru.obabok.mixin;

import org.spongepowered.asm.service.IGlobalPropertyService;
import org.spongepowered.asm.service.IPropertyKey;

import java.util.HashMap;
import java.util.Map;

public class MyPropertyService implements IGlobalPropertyService {
    private final Map<String, Object> properties = new HashMap<>();
    @Override
    public IPropertyKey resolveKey(String name) {
        return new IPropertyKey() { @Override public String toString() { return name; } };
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProperty(IPropertyKey key) { return (T) properties.get(key.toString()); }

    @Override
    public void setProperty(IPropertyKey key, Object value) { properties.put(key.toString(), value); }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProperty(IPropertyKey key, T defaultValue) {
        return (T) properties.getOrDefault(key.toString(), defaultValue);
    }

    @Override public String getPropertyString(IPropertyKey key, String defaultValue) {
        Object val = properties.get(key.toString());
        return val != null ? val.toString() : defaultValue;
    }
}

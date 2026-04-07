1) в призме создать инстанс 26.1.1
2) изменить->версия->добавить пустой файл
3) изменить пустой файл

linux:
```{
   "formatVersion": 1,
   "name": "SimpleLoader",
   "uid": "org.multimc.custom.simpleloader",
   "version": "1",
   "mainClass": "ru.obabok.Main",
   "libraries": [
   {
   "name": "ru.obabok:simpleloader:1",
   "MMC-absoluteUrl": "file:///home/alex/Repository/SimpleLoader/build/libs/SimpleLoader-1.0-SNAPSHOT.jar",
   "localOnly": true
   },
   {
   "name": "org.spongepowered:mixin:0.8.5",
   "MMC-absoluteUrl": "file:///home/alex/Repository/SimpleLoader/libs/mixin-0.8.5.jar",
   "localOnly": true
   },
   {
   "name": "org.ow2.asm:asm:9.9.1",
   "MMC-absoluteUrl": "file:///home/alex/Repository/SimpleLoader/libs/asm-9.9.1.jar",
   "localOnly": true
   },
   {
   "name": "org.ow2.asm:asm-tree:9.9.1",
   "MMC-absoluteUrl": "file:///home/alex/Repository/SimpleLoader/libs/asm-tree-9.9.1.jar",
   "localOnly": true
   },
   {
   "name": "org.ow2.asm:asm-commons:9.9.1",
   "MMC-absoluteUrl": "file:///home/alex/Repository/SimpleLoader/libs/asm-commons-9.9.1.jar",
   "localOnly": true
   },
   {
   "name": "org.ow2.asm:asm-util:9.9.1",
   "MMC-absoluteUrl": "file:///home/alex/Repository/SimpleLoader/libs/asm-util-9.9.1.jar",
   "localOnly": true
   },
   {
   "name": "org.ow2.asm:asm-analysis:9.9.1",
   "MMC-absoluteUrl": "file:///home/alex/Repository/SimpleLoader/libs/asm-analysis-9.9.1.jar",
   "localOnly": true
   }
   ],
   "order": -10
   }
```
windows:

```
{
    "formatVersion": 1,
   "name": "SimpleLoader",
   "uid": "org.multimc.custom.simpleloader",
   "version": "1",
   "mainClass": "ru.obabok.Main",
   "libraries": [
   {
   "name": "ru.obabok:simpleloader:1",
   "MMC-absoluteUrl": "file://E:/Programs/Repository/Loader/SimpleLoader/build/libs/SimpleLoader-1.0-SNAPSHOT.jar",
   "localOnly": true
   },
   {
   "name": "org.spongepowered:mixin:0.8.5",
   "MMC-absoluteUrl": "file://E:/Programs/Repository/Loader/SimpleLoader/libs/mixin-0.8.5.jar",
   "localOnly": true
   },
   {
   "name": "org.ow2.asm:asm:9.9.1",
   "MMC-absoluteUrl": "file://E:/Programs/Repository/Loader/SimpleLoader/libs/asm-9.9.1.jar",
   "localOnly": true
   },
   {
   "name": "org.ow2.asm:asm-tree:9.9.1",
   "MMC-absoluteUrl": "file://E:/Programs/Repository/Loader/SimpleLoader/libs/asm-tree-9.9.1.jar",
   "localOnly": true
   },
   {
   "name": "org.ow2.asm:asm-commons:9.9.1",
   "MMC-absoluteUrl": "file://E:/Programs/Repository/Loader/SimpleLoader/libs/asm-commons-9.9.1.jar",
   "localOnly": true
   },
   {
   "name": "org.ow2.asm:asm-util:9.9.1",
   "MMC-absoluteUrl": "file://E:/Programs/Repository/Loader/SimpleLoader/libs/asm-util-9.9.1.jar",
   "localOnly": true
   },
   {
   "name": "org.ow2.asm:asm-analysis:9.9.1",
   "MMC-absoluteUrl": "file://E:/Programs/Repository/Loader/SimpleLoader/libs/asm-analysis-9.9.1.jar",
   "localOnly": true
   }
   ],
   "order": -10
   }
}

```
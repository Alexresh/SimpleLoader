Загрузчик модов для minecraft. Поддерживает миксины
для игроков просто запустить SimpleLoader, ввести название инстанса и папку призм лаунчера


для разрабов
В IDE:
в build.gradle.kts поменять prismLibDir на ваш (там пути для винды и для линуха, меняйте под вашу систему)
В призм лаунчере:
1) создать инстанс через запуск файла(как для игроков)
2) изменить->версия->там будет в конце SimpleLoader, нажать на него
3) нажать изменить справа там где-то
4) в libraries там будет
```
{
    "name": "ru.obabok:simpleloader:1",
    "MMC-absoluteUrl": "file://{путь до вашего билда}",
    "localOnly": true
}
```
например "file:D:/Repository/SimpleLoader/build/libs/SimpleLoader-1.0.jar"
или "file:///home/alex/Repository/SimpleLoader/build/libs/SimpleLoader-1.0.jar"

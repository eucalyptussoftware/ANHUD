# ANHUD Control Intents

Экспортированный ресивер: `com.g992.anhud/.HudStatusReceiver`

Базовый шаблон:

```bash
adb shell am broadcast -n com.g992.anhud/.HudStatusReceiver -a ACTION_NAME ...
```

## `G992.ANHUD.STATUS`

Интент для управления состоянием HUD и отдельными UI-блоками.

Поддерживаемые extras:

- `ENABLE` (`bool|0/1|"true"/"false"`): включает или выключает HUD.
- `STOP_NAVIGATION` (`bool|0/1|"true"/"false"`): завершает активную навигацию.
- `NAV` (`bool|0/1|"true"/"false"`): показывает или скрывает навигационный блок.
- `SPEED_LIMIT` (`bool|0/1|"true"/"false"`): показывает или скрывает блок лимита скорости.
- `SPEED_ALERT` (`bool|0/1|"true"/"false"`): включает или выключает alert превышения скорости.
- `SPEED_ALERT_THRESHOLD` (`int`): порог alert превышения.
- `SPEEDOMETER` (`bool|0/1|"true"/"false"`): показывает или скрывает спидометр.
- `CLOCK` (`bool|0/1|"true"/"false"`): показывает или скрывает часы.
- `MAP` (`bool|0/1|"true"/"false"`): показывает или скрывает блок карты.

Поведение:

- `ENABLE=true` сработает только если у приложения есть permission на overlay.
- `ENABLE=false` выключает HUD и дополнительно останавливает активную навигацию.
- `STOP_NAVIGATION=true` останавливает маршрут, не выключая сам HUD.
- Можно передавать только те extras, которые нужно изменить.

Примеры:

```bash
adb shell am broadcast -n com.g992.anhud/.HudStatusReceiver -a G992.ANHUD.STATUS --ez ENABLE true
```

```bash
adb shell am broadcast -n com.g992.anhud/.HudStatusReceiver -a G992.ANHUD.STATUS --ez ENABLE false
```

```bash
adb shell am broadcast -n com.g992.anhud/.HudStatusReceiver -a G992.ANHUD.STATUS --ez STOP_NAVIGATION true
```

```bash
adb shell am broadcast -n com.g992.anhud/.HudStatusReceiver -a G992.ANHUD.STATUS --ez NAV false --ez SPEED_LIMIT true --ez SPEED_ALERT true --ei SPEED_ALERT_THRESHOLD 15
```

```bash
adb shell am broadcast -n com.g992.anhud/.HudStatusReceiver -a G992.ANHUD.STATUS --ez SPEEDOMETER true --ez CLOCK false
```

```bash
adb shell am broadcast -n com.g992.anhud/.HudStatusReceiver -a G992.ANHUD.STATUS --ez MAP true
```

Ограничения текущего контракта `G992.ANHUD.STATUS`:

- Управляет только `ENABLE`, `STOP_NAVIGATION` и частью простых toggle-настроек.
- Пока не умеет менять видимость следующих HUD-блоков: `LANE_GUIDANCE`, `ARROW`, `HUDSPEED`, `ROAD_CAMERA`, `TRAFFIC_LIGHT`, `TURN_SIGNALS`.
- Пока не умеет менять связанные опции вроде `HUDSPEED_LIMIT`, `HUDSPEED_LIMIT_ALERT`, `HUDSPEED_LIMIT_ALERT_THRESHOLD`, `SPEEDOMETER_SHOW_UNIT_TEXT`, `ARROW_ONLY_WHEN_NO_ICON`, `TRAFFIC_LIGHT_MAX_ACTIVE`.
- Не умеет менять layout/scale/alpha/position/preview-настройки.
- Для полного набора overlay-настроек сейчас используйте пресеты через `ANHUD_SET_PRESET`.

## `ANHUD_SET_PRESET`

Интент для применения пресета по номеру.

Поддерживаемые keys для номера пресета:

- `PRESET`
- `INDEX`
- `preset`
- `index`
- `preset_number`
- `PRESET_NUMBER`

Поведение:

- Нумерация пресетов начинается с `1`.
- Если пресет не найден или не применился, интент игнорируется.
- После успешного применения отправляется полный refresh overlay.

Примеры:

```bash
adb shell am broadcast -n com.g992.anhud/.HudStatusReceiver -a ANHUD_SET_PRESET --ei PRESET 1
```

```bash
adb shell am broadcast -n com.g992.anhud/.HudStatusReceiver -a ANHUD_SET_PRESET --ei INDEX 2
```

```bash
adb shell am broadcast -n com.g992.anhud/.HudStatusReceiver -a ANHUD_SET_PRESET --es preset 3
```

## `G992.ANHUD.SET_PRESET`

Легаси-алиас для `ANHUD_SET_PRESET`. Работает так же, отличается только `action`.

Примеры:

```bash
adb shell am broadcast -n com.g992.anhud/.HudStatusReceiver -a G992.ANHUD.SET_PRESET --ei PRESET 1
```

```bash
adb shell am broadcast -n com.g992.anhud/.HudStatusReceiver -a G992.ANHUD.SET_PRESET --ei PRESET_NUMBER 4
```

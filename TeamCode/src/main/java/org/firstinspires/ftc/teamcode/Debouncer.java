package org.firstinspires.ftc.teamcode;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import com.qualcomm.robotcore.hardware.Gamepad;

public class Debouncer extends Object {
    Gamepad gamepad = null;
    public Map<Settings.switch_names, Switch> switches = new HashMap<>();
    public Map<Settings.throttle_names, Throttle> throttles = new HashMap<>();

    Debouncer(Gamepad gp) {
        //System.out.println("A bouncer is born");
        gamepad = gp;

        switches.put(Settings.switch_names.a, new Switch(Settings.switch_names.a));
        switches.put(Settings.switch_names.b, new Switch(Settings.switch_names.b));
        switches.put(Settings.switch_names.x, new Switch(Settings.switch_names.x));
        switches.put(Settings.switch_names.y, new Switch(Settings.switch_names.y));

        switches.put(Settings.switch_names.dpad_down, new Switch(Settings.switch_names.dpad_down));
        switches.put(Settings.switch_names.dpad_left, new Switch(Settings.switch_names.dpad_left));
        switches.put(Settings.switch_names.dpad_right, new Switch(Settings.switch_names.dpad_right));
        switches.put(Settings.switch_names.dpad_up, new Switch(Settings.switch_names.dpad_up));

        throttles.put(Settings.throttle_names.left_stick_x, new Throttle(Settings.throttle_names.left_stick_x));
        throttles.put(Settings.throttle_names.left_stick_y, new Throttle(Settings.throttle_names.left_stick_y));

        throttles.put(Settings.throttle_names.right_stick_x, new Throttle(Settings.throttle_names.right_stick_x));
        throttles.put(Settings.throttle_names.right_stick_y, new Throttle(Settings.throttle_names.right_stick_y));

        throttles.put(Settings.throttle_names.right_trigger, new Throttle(Settings.throttle_names.right_trigger));
        throttles.put(Settings.throttle_names.left_trigger, new Throttle(Settings.throttle_names.left_trigger));

    }

    public void init(){

    }

    public void init_loop(){

    }

    public void start() {

    }

    public void loop() {
        // Switch s = null;
        // Throttle t = null;

        // For Loop for iterating Map
        /*
         * for (Map.Entry entry : switches.entrySet()) { s = (Switch) entry.getValue();
         * System.out.println(s.getName() + "\t\t" + s.getState() + "\t" +
         * s.getOneShotState()); }
         */

        /*
         * for (Map.Entry entry : throttles.entrySet()) { t = (Throttle)
         * entry.getValue(); System.out.println(entry.getKey() + " -> " + t.getName());
         * }
         */

        switches.get(Settings.switch_names.a).setState(gamepad.a);
        switches.get(Settings.switch_names.b).setState(gamepad.b);
        switches.get(Settings.switch_names.x).setState(gamepad.x);
        switches.get(Settings.switch_names.y).setState(gamepad.y);



    }
    public void stop () {

    }
}
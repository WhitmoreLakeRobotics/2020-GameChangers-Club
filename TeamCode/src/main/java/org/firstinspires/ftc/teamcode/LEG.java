package org.firstinspires.ftc.teamcode;


// A class to control the Lifter, Extender and Gripper to do pick and place operations


public class LEG extends BaseHardware {

    Lifter lift = null;
    ExtenderMove2Pos extender = null;
    Gripper gripper = null;


    public static final int CARRY_TICS = 50;

    public enum STAGE_MAJOR {
        IDLE,
        PICKING,
        PLACING
    }

    STAGE_MAJOR majorStage_Current = STAGE_MAJOR.IDLE;

    public enum STAGE_PICKING {
        OPENING,
        LIFTING,
        EXTENDING,
        LOWERING,
        CLOSING,
        CARRY_POS
    }

    STAGE_PICKING pickingStage_Current = STAGE_PICKING.OPENING;

    public enum STAGE_PLACING {
        OPENING,
        LIFTING,
        RETRACTING,
        LOWERING
    }

    STAGE_PLACING placingStage_Current = STAGE_PLACING.OPENING;


    LEG(Lifter lft, ExtenderMove2Pos ext, Gripper grip) {
        lift = lft;
        extender = ext;
        gripper = grip;
    }

    public void init() {

    }

    public void init_loop() {

    }

    public void start() {

    }

    public void loop() {

        switch (majorStage_Current) {
            case IDLE:
                break;

            case PICKING:
                pick_loop();
                break;

            case PLACING:
                place_loop();
                break;

            default:
                break;
        }


    }

    // loop just for the pick operation
    private void pick_loop() {


        if (pickingStage_Current == STAGE_PICKING.LIFTING) {
            if (lift.getPosTics() > lift.getIndexTics(0)){
                pickingStage_Current = STAGE_PICKING.EXTENDING;
            }
        }

        if (pickingStage_Current == STAGE_PICKING.EXTENDING) {
            if (gripper.getIsOpen() && (lift.getPosTics() > lift.getIndexTics(lift.PRE_PICK_POS))) {
                lift.setPosition(lift.PICK_POS);
                pickingStage_Current = STAGE_PICKING.LOWERING;
            }
        }

        if (pickingStage_Current == STAGE_PICKING.LOWERING){
            if (lift.isInPosition(lift.PICK_POS)){
                gripper.cmd_close();
                pickingStage_Current = STAGE_PICKING.CLOSING;
            }
        }

        if (pickingStage_Current == STAGE_PICKING.CLOSING) {
            if (gripper.getIsClosed()) {
                pickingStage_Current = STAGE_PICKING.CARRY_POS;
                lift.setPosition(lift.CARRY_POS);
            }
        }

        if (pickingStage_Current == STAGE_PICKING.CARRY_POS){
            if (lift.isInPosition(lift.CARRY_POS)){
                // We are done picking
                majorStage_Current = STAGE_MAJOR.IDLE;
                gripper.underLEGControl = false;
                lift.underLEGControl = false;
                extender.underLEGControl = false;
            }
        }

    }


    // loop just for place operation
    private void place_loop() {

    }

    public void stop() {

    }

    public void pick() {
        majorStage_Current = STAGE_MAJOR.PICKING;
        pickingStage_Current = STAGE_PICKING.LIFTING;

        lift.underLEGControl = true;
        extender.underLEGControl = true;
        gripper.underLEGControl = true;
        gripper.cmd_open();
        lift.setPosition(lift.PRE_PICK_POS);
    }

    public void place() {

    }

}

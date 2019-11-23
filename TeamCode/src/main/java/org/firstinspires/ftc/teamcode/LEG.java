package org.firstinspires.ftc.teamcode;


// A class to control the Lifter, Extender and Gripper to do pick and place operations


public class LEG extends BaseHardware {

    Lifter lift = null;
    ExtenderMove2Pos extender = null;
    Gripper gripper = null;
    int clear_tower_tics = 0;

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
        LIFTING,
        RETRACTING,
        LOWERING
    }

    STAGE_PLACING placingStage_Current = STAGE_PLACING.LIFTING;


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
            if (lift.getPosTics() > lift.getIndexTics(lift.PRE_PICK_POS)) {
                pickingStage_Current = STAGE_PICKING.EXTENDING;
                extender.setPosition(extender.PICK);
            }
            else if (CommonLogic.inRange(lift.getPosTics(), lift.getIndexTics(lift.PRE_PICK_POS), Lifter.LIFTERPOS_TOL)) {
                pickingStage_Current = STAGE_PICKING.EXTENDING;
                extender.setPosition(extender.PICK);
            }
        }

        if (pickingStage_Current == STAGE_PICKING.EXTENDING) {
            if (gripper.getIsOpen() && (CommonLogic.inRange(lift.getPosTics(), lift.getIndexTics(lift.PRE_PICK_POS), Lifter.LIFTERPOS_TOL))) {
                lift.setPosition(lift.PICK_POS);
                pickingStage_Current = STAGE_PICKING.LOWERING;
            }
        }

        if (pickingStage_Current == STAGE_PICKING.LOWERING) {
            if (lift.isInPosition(lift.PICK_POS)) {
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

        if (pickingStage_Current == STAGE_PICKING.CARRY_POS) {
            if (lift.isInPosition(lift.CARRY_POS)) {
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

        if (placingStage_Current == STAGE_PLACING.LIFTING) {
            if (CommonLogic.inRange(lift.getPosTics(), clear_tower_tics, lift.LIFTERPOS_TOL)) {
                placingStage_Current = STAGE_PLACING.RETRACTING;
                extender.setPosition(extender.HOME);
            }
        }

        if (placingStage_Current == STAGE_PLACING.RETRACTING){
            if (CommonLogic.inRange(extender.getPosTics(),extender.getIndexTics(ExtenderMove2Pos.HOME), ExtenderMove2Pos.EXTENDER_POS_TOL)) {
                placingStage_Current = STAGE_PLACING.LOWERING;
                lift.setPosition(lift.PRE_PICK_POS);
            }
        }

        if (placingStage_Current == STAGE_PLACING.LOWERING) {
            if (CommonLogic.inRange(lift.getPosTics(),lift.getIndexTics(lift.PRE_PICK_POS),lift.LIFTERPOS_TOL)) {
                majorStage_Current = STAGE_MAJOR.IDLE;
                gripper.underLEGControl = false;
                extender.underLEGControl = false;
                lift.underLEGControl = false;
            }
        }
    }

    public void stop() {
        lift.stop();
        extender.stop();
        gripper.stop();
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
        majorStage_Current = STAGE_MAJOR.PLACING;
        placingStage_Current = STAGE_PLACING.LIFTING;

        lift.underLEGControl = true;
        extender.underLEGControl = true;
        gripper.underLEGControl = true;
        gripper.cmd_open();
        clear_tower_tics = lift.clear_tower();
    }

}

package org.firstinspires.ftc.teamcode;


// A class to control the Lifter, Extender and Gripper to do pick and place operations


public class LEG extends BaseHardware {

    public static final int CARRY_TICS = 50;
    Lifter lift = null;
    ExtenderMove2Pos extender = null;
    Gripper gripper = null;
    int clear_tower_tics = 0;
    int lift_pos_ticks = 0;
    int extend_pos_ticks = 0;
    STAGE_MAJOR majorStage_Current = STAGE_MAJOR.IDLE;
    STAGE_PICKING pickingStage_Current = STAGE_PICKING.OPENING;
    STAGE_PLACING placingStage_Current = STAGE_PLACING.LIFTING;

    LEG(Lifter lft, ExtenderMove2Pos ext, Gripper grip) {
        lift = lft;
        extender = ext;
        gripper = grip;
    }

    //*********************************************************************************************
    public void init() {

    }

    //*********************************************************************************************
    public void init_loop() {

    }

    //*********************************************************************************************
    public void start() {

    }

    public void loop() {

        switch (majorStage_Current) {
            case IDLE:
                gripper.underLEGControl = false;
                extender.underLEGControl = false;
                lift.underLEGControl = false;

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

    private void pick_loop() {

        lift_pos_ticks = lift.getPosTics();
        extend_pos_ticks = extender.getPosTics();

        if (pickingStage_Current == STAGE_PICKING.LIFTING) {

            if (lift_pos_ticks > lift.getIndexTics(Lifter.PRE_PICK_POS)) {
                pickingStage_Current = STAGE_PICKING.EXTENDING;
                extender.setPosition(ExtenderMove2Pos.PICK);
            } else if (lift.isInPosition(Lifter.PRE_PICK_POS)) {
                pickingStage_Current = STAGE_PICKING.EXTENDING;
                extender.setPosition(ExtenderMove2Pos.PICK);
            }
        }

        if (pickingStage_Current == STAGE_PICKING.EXTENDING) {
            if (extender.isInPosition(ExtenderMove2Pos.PICK) && gripper.getIsOpen()) {
                lift.setPosition(Lifter.PICK_POS);
                pickingStage_Current = STAGE_PICKING.LOWERING;
            }
        }

        if (pickingStage_Current == STAGE_PICKING.LOWERING) {
            if (lift.isInPosition(Lifter.PICK_POS)) {
                gripper.cmd_close();
                pickingStage_Current = STAGE_PICKING.CLOSING;
            }
        }

        if (pickingStage_Current == STAGE_PICKING.CLOSING) {
            if (gripper.getIsClosed()) {
                pickingStage_Current = STAGE_PICKING.CARRY_POS;
                lift.setPosition(Lifter.CARRY_POS);
            }
        }

        if (pickingStage_Current == STAGE_PICKING.CARRY_POS) {
            if (lift.isInPosition(Lifter.CARRY_POS)) {
                // We are done picking
                majorStage_Current = STAGE_MAJOR.IDLE;
                gripper.underLEGControl = false;
                lift.underLEGControl = false;
                extender.underLEGControl = false;
            }
        }
    }

    //*********************************************************************************************
    // loop just for place operation
    private void place_loop() {

        lift_pos_ticks = lift.getPosTics();
        extend_pos_ticks = extender.getPosTics();

        if (placingStage_Current == STAGE_PLACING.EXTENDING) {
            if (extender.isInPosition(ExtenderMove2Pos.PLACE_1)) {
                placingStage_Current = STAGE_PLACING.LIFTING;
                gripper.cmd_open();
                clear_tower_tics = lift.clear_tower();
            }
        }

        if (placingStage_Current == STAGE_PLACING.LIFTING) {
            if (CommonLogic.inRange(lift_pos_ticks, clear_tower_tics, Lifter.LIFTERPOS_TOL) ||
                    lift_pos_ticks > clear_tower_tics) {
                placingStage_Current = STAGE_PLACING.RETRACTING;
                extender.setPosition(ExtenderMove2Pos.HOME);
            }
        }

        if (placingStage_Current == STAGE_PLACING.RETRACTING) {
            if (extender.isInPosition(ExtenderMove2Pos.HOME) ||
                    extend_pos_ticks < extender.getIndexTics(ExtenderMove2Pos.PICK)) {
                placingStage_Current = STAGE_PLACING.LOWERING;
                lift.setPosition(Lifter.PICK_POS);
            }
        }

        if (placingStage_Current == STAGE_PLACING.LOWERING) {

            if (lift.isInPosition(Lifter.PRE_PICK_POS)) {
                majorStage_Current = STAGE_MAJOR.IDLE;
                gripper.underLEGControl = false;
                extender.underLEGControl = false;
                lift.underLEGControl = false;
            }
        }
    }
    //*********************************************************************************************

    //*********************************************************************************************
    public boolean getcmdComplete() {
        return (majorStage_Current == STAGE_MAJOR.IDLE);
    }

    //*********************************************************************************************
    // loop just for the pick operation

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
        lift.setPosition(Lifter.PRE_PICK_POS);
    }

    public void place() {

        majorStage_Current = STAGE_MAJOR.PLACING;
        lift.underLEGControl = true;
        extender.underLEGControl = true;
        gripper.underLEGControl = true;

        extend_pos_ticks = extender.getPosTics();

        // If we are only at Pick location on the extender then we can not drop the stone yet.
        if (CommonLogic.inRange(extend_pos_ticks, extender.getIndexTics(ExtenderMove2Pos.PLACE_1), ExtenderMove2Pos.EXTENDER_POS_TOL) ||
                (extend_pos_ticks > extender.getIndexTics(ExtenderMove2Pos.PLACE_1))) {
            placingStage_Current = STAGE_PLACING.LIFTING;
            gripper.cmd_open();
            clear_tower_tics = lift.clear_tower();
        } else {
            placingStage_Current = STAGE_PLACING.EXTENDING;
            extender.setPosition(ExtenderMove2Pos.PLACE_1);
        }
    }
    //*********************************************************************************************

    public enum STAGE_MAJOR {
        IDLE,
        PICKING,
        PLACING
    }

    //*********************************************************************************************

    public enum STAGE_PICKING {
        OPENING,
        LIFTING,
        EXTENDING,
        LOWERING,
        CLOSING,
        CARRY_POS
    }

    //*********************************************************************************************

    public enum STAGE_PLACING {
        EXTENDING,
        LIFTING,
        RETRACTING,
        LOWERING
    }
}

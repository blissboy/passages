package com.boyamihungry.passageways;

import controlP5.*;
import processing.core.PApplet;
import processing.core.PFont;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * Created by patwheaton on 9/20/16.
 */
public class Passageways extends PApplet implements ControlListener {

    public static final int WIDTH = 1080;
    public static final int HEIGHT = 1080;

    PFont arialNar16;
    PFont arialNar12;
    PFont arial14;


    // style whatnot
    private int frameStroke = 0;
    private int frameStrokeWeight = 4;

    /////// control panel whatnot ///////
    private int ui_unknownExitRadius = 100;
    private int ui_fromExitSize = 1;
    private int ui_toExitSize = 300;
    private int ui_myWorldRadius = 1000;
    private int ui_depthOfPassage = 300;
    private int ui_wallSwirlCount = 255;
    private int ui_wallSwirlRadius = 225;
    private int ui_depthLayers = 5;
    private int ui_g_offset = 50;
    private int ui_b_offset = 50;

    private boolean ui_exitSize = false;

    private int ui_sys_cpWidth = 200;
    private int ui_sys_margin = 20;
    private int ui_sys_vertSpacing = 30;
    private int ui_sys_horizSpacing = 30;
    private int ui_sys_controlHeight = 20;
    private int ui_sys_sliderWidth = 200;
    private int ui_sys_textFieldWidth = 80;
    private int ui_sys_textFieldWordMargin = 10;
    private int ui_sys_labelWidth = 60;
    private int ui_sys_toLabelWidth = 20;
    private int ui_sys_toggleWidth = 40;
    private int ui_sys_dropdownWidth = 200;
    private int ui_sys_dropdown_height = 100;
    private int ui_sys_nameField_width = 100;


    private int ui_sys_oscillators_y_location = 0; //bleh
    private boolean ui_sys_readyForOperation;

    private Map<String, Set<Controller>> ui_controlGroups = new HashMap<>();
    private Map<String, List<ValueSetter>> groupValueSetters = new HashMap<>();
    private List<ValueSetter> currentValueSetters = new ArrayList<>();
    // herehere need to create value setters for each variable for both the slider and oscil states

    /* thoughts on structure of control groups
    - have list of controls that are visible per state
    - likely have two states, could be more complex
    - have functions that are run depending on state
        - example, if state is "use oscillator, fields are set in different way, if state is "use slider", different way
    - possible implementations
        - Map<String (state),
              Map<String (functionType like "turn on fields", "show components", "do everything"),
                  Callable<String (@nullable stateChange)>>
     */
    private Map<String, Map<String,Callable<String>>> stateMap = new HashMap<>();
    private Set<String> activeStates = new HashSet<>();

    private Map<String,Oscillator> ui_oscillators = new HashMap<>();
    static final String NEW_OSCILLATOR = "Create new...";
    static final String UI_OSCIL_SELECTOR = "ui_select_osc";
    static final String UI_VAL_FROM = "ui_valFrom_";
    static final String UI_VAL_TO = "ui_valTo_";
    static final String UI_TOGGLE = "ui_tog_";
    private ControlP5 cp5;



    public void settings()  {
        size(WIDTH,HEIGHT);
    }


    public void setup() {

        ui_sys_readyForOperation = false;
        ui_oscillators.put("osc 0", new SinusoidalOscillator(6000));
        ui_oscillators.put("osc 1", new SinusoidalOscillator(6000));
        arialNar12 = createFont("ArialNarrow", 12);
        arialNar16 = createFont("ArialNarrow", 16);
        arial14 = createFont("Arial", 14);

        setupControlPanel();
        ui_sys_readyForOperation = true;
        //printArray(PFont.list());
    }

    private void updateVariables() {
        // for each variable name

        // get the function to set the value for that vaariable

        // call the function
    }


    public void draw() {

        background(128);
        pushMatrix();
        translate(width/2, height/2);
        pushStyle();

        stroke(255);
        strokeWeight(3f);

        // exit
        ellipse(0,0,ui_unknownExitRadius * 2, ui_unknownExitRadius * 2);

        // worldradius
        noFill();
        ellipse(0,0,ui_myWorldRadius,ui_myWorldRadius);

        strokeWeight(1f);

        float r = ui_myWorldRadius - ui_unknownExitRadius;
        float rStep = r / (ui_depthLayers + 1);
        int adornmentCount;

        float theta = 0;


        // layers
        for ( int layerCount=0; layerCount<ui_depthLayers; layerCount++) {
            adornmentCount = 0;
            theta = 0;

            do {
                ellipse(((ui_unknownExitRadius / 2) + (rStep * layerCount) ) * cos(theta),
                        ((ui_unknownExitRadius / 2) + (rStep * layerCount) ) * sin(theta),
                        ui_wallSwirlRadius,
                        ui_wallSwirlRadius);
                theta = theta + ((2f * PI) / ((float)ui_wallSwirlCount/(float)ui_depthLayers));

                adornmentCount++;

            } while (adornmentCount < (ui_wallSwirlCount / ui_depthLayers ));

        }

        popStyle();
        popMatrix();

    }

    void drawControlPanelArea() {
        pushStyle();
        stroke(frameStroke);
        strokeWeight(frameStrokeWeight);
        line(ui_sys_cpWidth,0, ui_sys_cpWidth,HEIGHT);
    }


    void setupControlPanel() {

        int controlCount = 0;

        cp5 = new ControlP5(this);


        int currentX = ui_sys_margin;

        cp5.addToggle(UI_TOGGLE + "ui_exitSize")
                .setPosition(currentX, (ui_sys_margin + ui_sys_controlHeight) * ++controlCount)
                .setSize(ui_sys_toggleWidth, ui_sys_controlHeight)
                .setValue(ui_exitSize)
                .setLabelVisible(false);
        currentX += ui_sys_toggleWidth;
        cp5.addLabel("Exit size")
                .setPosition(currentX, (ui_sys_margin + ui_sys_controlHeight) * controlCount)
                .setSize(ui_sys_labelWidth, ui_sys_controlHeight)
                .setFont(arialNar16);
        currentX += ui_sys_labelWidth;
        ControlP5Helper.addControlToGroup( UI_TOGGLE + "ui_exitSize-false",
                cp5.addSlider("ui_unknownExitRadius")
                        .setPosition(currentX, (ui_sys_margin + ui_sys_controlHeight) * controlCount)
                        .setSize(ui_sys_sliderWidth, ui_sys_controlHeight)
                        .setRange(1, 100)
                        .setValue(ui_unknownExitRadius)
                        .setLabelVisible(false)
                        .setVisible(!ui_exitSize),
                ui_controlGroups);



        // don't update currentX, stay in the same place
        ControlP5Helper.addControlToGroup( UI_TOGGLE + "ui_exitSize-true",
                cp5.addTextfield("ui_fromExitSize")
                        .setPosition(currentX, (ui_sys_margin + ui_sys_controlHeight) * controlCount)
                        .setSize(ui_sys_textFieldWidth, ui_sys_controlHeight)
                        .setValue(ui_fromExitSize)
                        .setLabel("")  // setting label non-visible does not work
                        .setVisible(ui_exitSize),
                ui_controlGroups);
        currentX += ui_sys_textFieldWidth;
        ControlP5Helper.addControlToGroup( UI_TOGGLE + "ui_exitSize-true",
                cp5.addLabel("to")
                        .setPosition(currentX, (ui_sys_margin + ui_sys_controlHeight) * controlCount)
                        .setSize(ui_sys_toLabelWidth, ui_sys_controlHeight)
                        .setFont(arialNar16)
                        .setVisible(ui_exitSize),
                ui_controlGroups);
        currentX += ui_sys_toLabelWidth;
        ControlP5Helper.addControlToGroup(  UI_TOGGLE + "ui_exitSize-true",
                cp5.addTextfield("ui_toExitSize")
                        .setPosition(currentX, (ui_sys_margin + ui_sys_controlHeight) * controlCount)
                        .setSize(ui_sys_textFieldWidth, ui_sys_controlHeight)
                        .setValue(ui_toExitSize)
                        .setLabel("")  // setting label non-visible does not work
                        .setVisible(ui_exitSize),
                ui_controlGroups);
        currentX += ui_sys_textFieldWidth + ui_sys_margin;

        ControlP5Helper.addControlToGroup( UI_TOGGLE + "ui_exitSize-true",
                cp5.addScrollableList(UI_OSCIL_SELECTOR + "exitSize")
                        .setPosition(currentX, (ui_sys_margin + ui_sys_controlHeight) * controlCount)
                        .setSize(ui_sys_dropdownWidth, ui_sys_dropdown_height)
                        .setBarHeight(ui_sys_controlHeight)
                        .setItemHeight(ui_sys_controlHeight)
                        .addItem(NEW_OSCILLATOR,NEW_OSCILLATOR)
                        .addItems(ui_oscillators.keySet().stream().collect(Collectors.toList()))
                        .setFont(arialNar12)
                        .setLabel("select oscillator...")  // setting label non-visible does not work
                        .setVisible(ui_exitSize)
                        .setOpen(false)
                        .bringToFront(),
                ui_controlGroups);

        cp5.addLabel("World radius")
                .setPosition(ui_sys_margin, (ui_sys_margin + ui_sys_controlHeight) * ++controlCount)
                .setSize(ui_sys_labelWidth, ui_sys_controlHeight)
                .setFont(arialNar16);
        cp5.addSlider("ui_myWorldRadius")
                .setPosition(ui_sys_margin + ui_sys_labelWidth, (ui_sys_margin + ui_sys_controlHeight) * controlCount)
                .setSize(ui_sys_sliderWidth, ui_sys_controlHeight)
                .setRange(1, 2000)
                .setValue(ui_myWorldRadius)
                .setLabelVisible(false);
        cp5.addLabel("Depth")
                .setPosition(ui_sys_margin, (ui_sys_margin + ui_sys_controlHeight) * ++controlCount)
                .setSize(ui_sys_labelWidth, ui_sys_controlHeight)
                .setFont(arialNar16);
        cp5.addSlider("ui_depthOfPassage")
                .setPosition(ui_sys_margin + ui_sys_labelWidth, (ui_sys_margin + ui_sys_controlHeight) * controlCount)
                .setSize(ui_sys_sliderWidth, ui_sys_controlHeight)
                .setRange(1, 1000)
                .setValue(ui_depthOfPassage)
                .setLabelVisible(false);
        cp5.addLabel("Layers")
                .setPosition(ui_sys_margin, (ui_sys_margin + ui_sys_controlHeight) * ++controlCount)
                .setSize(ui_sys_labelWidth, ui_sys_controlHeight)
                .setFont(arialNar16);
        cp5.addSlider("ui_depthLayers")
                .setPosition(ui_sys_margin + ui_sys_labelWidth, (ui_sys_margin + ui_sys_controlHeight) * controlCount)
                .setSize(ui_sys_sliderWidth, ui_sys_controlHeight)
                .setRange(1, 100)
                .setValue(ui_depthLayers)
                .setLabelVisible(false);
        cp5.addLabel("swirl radius")
                .setPosition(ui_sys_margin, (ui_sys_margin + ui_sys_controlHeight) * ++controlCount)
                .setSize(ui_sys_labelWidth, ui_sys_controlHeight)
                .setFont(arialNar16);
        cp5.addSlider("ui_wallSwirlRadius")
                .setPosition(ui_sys_margin + ui_sys_labelWidth, (ui_sys_margin + ui_sys_controlHeight) * controlCount)
                .setSize(ui_sys_sliderWidth, ui_sys_controlHeight)
                .setRange(1, 500)
                .setValue(ui_wallSwirlRadius)
                .setLabelVisible(false);
        cp5.addLabel("swirl count")
                .setPosition(ui_sys_margin, (ui_sys_margin + ui_sys_controlHeight) * ++controlCount)
                .setSize(ui_sys_labelWidth, ui_sys_controlHeight)
                .setFont(arialNar16);
        cp5.addSlider("ui_wallSwirlCount")
                .setPosition(ui_sys_margin + ui_sys_labelWidth, (ui_sys_margin + ui_sys_controlHeight) * controlCount)
                .setSize(ui_sys_sliderWidth, ui_sys_controlHeight)
                .setRange(1, 1500)
                .setValue(ui_wallSwirlCount)
                .setLabelVisible(false);
        cp5.addLabel("r offset")
                .setPosition(ui_sys_margin, (ui_sys_margin + ui_sys_controlHeight) * ++controlCount)
                .setSize(ui_sys_labelWidth, ui_sys_controlHeight)
                .setFont(arialNar16);
        cp5.addSlider("ui_r_offset")
                .setPosition(ui_sys_margin + ui_sys_labelWidth, (ui_sys_margin + ui_sys_controlHeight) * controlCount)
                .setSize(ui_sys_sliderWidth, ui_sys_controlHeight)
                .setRange(1, 100)
                .setValue(ui_g_offset)
                .setLabelVisible(false);
        cp5.addLabel("b offset")
                .setPosition(ui_sys_margin, (ui_sys_margin + ui_sys_controlHeight) * ++controlCount)
                .setSize(ui_sys_labelWidth, ui_sys_controlHeight)
                .setFont(arialNar16);
        cp5.addSlider("ui_b_offset")
                .setPosition(ui_sys_margin + ui_sys_labelWidth, (ui_sys_margin + ui_sys_controlHeight) * controlCount)
                .setSize(ui_sys_sliderWidth, ui_sys_controlHeight)
                .setRange(1, 100)
                .setValue(ui_b_offset);

        controlCount++;
        ui_sys_oscillators_y_location = (ui_sys_margin + ui_sys_controlHeight) * ++controlCount;
        // Display oscillators
        drawOscillatorsUI(ui_sys_margin, ui_sys_oscillators_y_location);

    }



    private void drawOscillatorsUI(int x, int y) {

        int currentX = x;
        int currentY = y;
        int oscCount = 0;

        for ( String key : ui_oscillators.keySet() ) {
            if (!NEW_OSCILLATOR.equals(key)) {

                currentX = x;
                cp5.addTextarea("ui_osc_name_" + key)    // todo: make constant
                        .setPosition(currentX + ui_sys_margin, currentY)
                        .setSize(ui_sys_nameField_width, ui_sys_controlHeight)
                        .setText(key)
                        .setFont(arial14);
                currentX += ui_sys_margin + ui_sys_nameField_width;
                cp5.addLabel("oscFreq" + key)
                        .setPosition(currentX + ui_sys_margin, currentY)
                        .setSize(ui_sys_labelWidth, ui_sys_controlHeight)
                        .setText("frequency");
                currentX += ui_sys_labelWidth + ui_sys_margin;
                cp5.addTextarea("ui_osc_freq_" + key)    // todo: make constant
                        .setPosition(currentX + ui_sys_margin, currentY)
                        .setSize(ui_sys_textFieldWidth, ui_sys_controlHeight)
                        .setText(String.valueOf(ui_oscillators.get(key).getFrequency()))
                        .setFont(arial14);
                System.out.println("@#$@#$@#$@#$@#$@#$@#$@# " + ui_oscillators.get(key).getFrequency());
                currentX += ui_sys_margin + ui_sys_textFieldWidth;

                currentY += ui_sys_margin + ui_sys_controlHeight;


            }
        }

    }

    @Override
    public void controlEvent(ControlEvent controlEvent) {
        System.out.println(controlEvent.toString());

        if ( ui_sys_readyForOperation ) {
            // turn off group's visibility on toggle
            Controller c = controlEvent.getController();
            if (c instanceof Toggle && c.getName().startsWith(UI_TOGGLE)) {
                setGroupVisibility(Optional.of(ui_controlGroups.get(c.getName() + "-true")),
                        ((Toggle) c).getBooleanValue());
                setGroupVisibility(Optional.of(ui_controlGroups.get(c.getName() + "-false")),
                        ! ((Toggle) c).getBooleanValue());

            } else if (controlEvent.getController() instanceof ScrollableList) {
                if ( c.getName().startsWith(UI_OSCIL_SELECTOR)) {
                    if (! ((ScrollableList)c).getItem((int)c.getValue()).get("name").equals(NEW_OSCILLATOR) ) {
                        // this is honestly the best way I have found to get this :-/
                        Oscillator o = ui_oscillators.get(((ScrollableList) c).getItem((int) c.getValue()).get("name"));
                        if ( null != o ) {

                        }
                    } else {
                        // this means a variable is being requested to associate with an oscillator
                        String variableName = c.getName().substring(UI_OSCIL_SELECTOR.length());

                    }
                }
            }


        }

    }




    private void setGroupVisibility(Optional<Collection<Controller>> controllersOptional, boolean visible) {
        controllersOptional.ifPresent(controllers -> controllers.forEach(c -> {
            c.setVisible(visible);
            if (c instanceof ScrollableList) {
                ((ScrollableList) c).bringToFront();
            }
        }));

    }


    static public void main(String[] passedArgs) {
        String[] appletArgs = new String[]{"com.boyamihungry.passageways.Passageways"};
        if (passedArgs != null) {
            PApplet.main(concat(appletArgs, passedArgs));
        } else {
            PApplet.main(appletArgs);
        }
    }



}

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

    // variables that control what happens as output
    private Float exitSize = 100f;


    /////// control panel whatnot ///////
    private float ui_fromExitSizeInitial = 1;
    private float ui_toExitSizeInitial = 300;
    private float ui_myWorldRadius = 1000;
    private float ui_depthOfPassage = 300;
    private float ui_wallSwirlCount = 255;
    private float ui_wallSwirlRadius = 225;
    private float ui_depthLayers = 5;
    private float ui_g_offset = 50;
    private float ui_b_offset = 50;

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

    //BasicStateEngine stateEngine = new BasicStateEngine();

    private Map<String,Callable<Optional<List<StateEngine.StateChange>>>> oncePerFrameMethods = new HashMap<>();


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
         ----- changed to just be
        - Map<String (state), Callable<String (@nullable stateChange)>>


     */
    private Map<String, Callable<Optional<List<StateEngine.StateChange>>>> stateMap = new HashMap<>();
    private Set<String> activeStates = new HashSet<>();

    private Map<String,Oscillator> ui_oscillators = new HashMap<>();
    static final String NEW_OSCILLATOR = "Create new...";
    static final String UI_OSCIL_SELECTOR = "ui_select_osc";
    static final String UI_VAL_FROM = "ui_valFrom_";
    static final String UI_VAL_TO = "ui_valTo_";
    static final String UI_TOGGLE = "ui_tog_";

    static final String UI_OSC_NAME = "ui_osc_name_";
    static final String UI_OSC_FREQ_VAL = "ui_osc_freq_val";
    static final String UI_OSC_FREQ_LABEL = "ui_osc_freq_label";

    private ControlP5 cp5;



    public void settings()  {
        size(WIDTH,HEIGHT);
    }


    public void setup() {

        ui_sys_readyForOperation = false;
        ui_oscillators.put("osc 0", new SinusoidalOscillator(
                () -> {
                    Float value;
                    try {
                        System.out.print("getting value-pre");
                        Textfield oscFreq = (Textfield) cp5.get(UI_OSC_FREQ_VAL + "osc 0");
                        if ( null != oscFreq ) {
                            value = Float.valueOf(oscFreq.getText());
                        } else {
                            System.out.print(" - null textfield - ");
                            value = Oscillator.DEFAULT_FREQUENCY;
                        }
                        return value;
                    } catch (NumberFormatException nfE) {
                        nfE.printStackTrace();
                        value = Oscillator.DEFAULT_FREQUENCY;
                    }
                    System.out.println("\tgot value: freq = " + value);
                    return value;
                }
        ));
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
        oncePerFrameMethods.values().stream().forEach(c -> {
            try {
                c.call();
            } catch (Exception e ) {
                throw new RuntimeException(e);
            }
        });
        background(128);
        pushMatrix();
        translate(width / 2, height / 2);
        pushStyle();

        stroke(255);
        strokeWeight(3f);

        // exit
        ellipse(0, 0, exitSize * 2, exitSize * 2);

        // worldradius
        noFill();
        ellipse(0, 0, ui_myWorldRadius, ui_myWorldRadius);

        strokeWeight(1f);

        float r = ui_myWorldRadius - exitSize;
        float rStep = r / (ui_depthLayers + 1);
        int adornmentCount;

        float theta = 0;


        // layers
        for (int layerCount = 0; layerCount < ui_depthLayers; layerCount++) {
            adornmentCount = 0;
            theta = 0;

            do {
                ellipse(((exitSize / 2) + (rStep * layerCount)) * cos(theta),
                        ((exitSize / 2) + (rStep * layerCount)) * sin(theta),
                        ui_wallSwirlRadius,
                        ui_wallSwirlRadius);
                theta = theta + ((2f * PI) / ((float) ui_wallSwirlCount / (float) ui_depthLayers));

                adornmentCount++;

            } while (adornmentCount < (ui_wallSwirlCount / ui_depthLayers));

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

        cp5 = new ControlP5(this);

        int currentX = ui_sys_margin;
        int controlCount = 0;

        // --------------------------------
        // ---- exit size
        String varName = "exitSize";
        cp5.addToggle(UI_TOGGLE + varName)
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
        ControlP5Helper.addControlToGroup( varName + "-false",
                cp5.addSlider(varName)
                        .setPosition(currentX, (ui_sys_margin + ui_sys_controlHeight) * controlCount)
                        .setSize(ui_sys_sliderWidth, ui_sys_controlHeight)
                        .setRange(1, 100)
                        .setValue(exitSize)
                        .setLabelVisible(false)
                        .setVisible(!ui_exitSize),
                ui_controlGroups);

        // don't update currentX, stay in the same place
        ControlP5Helper.addControlToGroup(varName + "-true",
                cp5.addTextfield(UI_VAL_FROM + varName)
                        .setPosition(currentX, (ui_sys_margin + ui_sys_controlHeight) * controlCount)
                        .setSize(ui_sys_textFieldWidth, ui_sys_controlHeight)
                        .setValue(ui_fromExitSizeInitial)
                        .setLabel("")  // setting label non-visible does not work
                        .setVisible(ui_exitSize),
                ui_controlGroups);
        currentX += ui_sys_textFieldWidth;
        ControlP5Helper.addControlToGroup(varName + "-true",
                cp5.addLabel("to")
                        .setPosition(currentX, (ui_sys_margin + ui_sys_controlHeight) * controlCount)
                        .setSize(ui_sys_toLabelWidth, ui_sys_controlHeight)
                        .setFont(arialNar16)
                        .setVisible(ui_exitSize),
                ui_controlGroups);
        currentX += ui_sys_toLabelWidth;
        ControlP5Helper.addControlToGroup(varName + "-true",
                cp5.addTextfield(UI_VAL_TO + varName)
                        .setPosition(currentX, (ui_sys_margin + ui_sys_controlHeight) * controlCount)
                        .setSize(ui_sys_textFieldWidth, ui_sys_controlHeight)
                        .setValue(ui_toExitSizeInitial)
                        .setLabel("")  // setting label non-visible does not work
                        .setVisible(ui_exitSize),
                ui_controlGroups);
        currentX += ui_sys_textFieldWidth + ui_sys_margin;

        ControlP5Helper.addControlToGroup(varName + "-true",
                cp5.addScrollableList(UI_OSCIL_SELECTOR + varName)
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

        // --------------------------------
        // ---- world radius
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

        //createControlGroupStates(ui_controlGroups);


        controlCount++;
        ui_sys_oscillators_y_location = (ui_sys_margin + ui_sys_controlHeight) * ++controlCount;
        // Display oscillators
        drawOscillatorsUI(ui_sys_margin, ui_sys_oscillators_y_location);

    }


    /**
     * Draws the avail oscillators at a point
     * @param x
     * @param y
     */
    private void drawOscillatorsUI(int x, int y) {

        if ( true) {
            int currentX = x;
            int currentY = y;
            int oscCount = 0;

            for (String key : ui_oscillators.keySet()) {
                if (!NEW_OSCILLATOR.equals(key)) {

                    currentX = x;
                    Textarea name = cp5.get(Textarea.class, UI_OSC_NAME + key);
                    if (null == name) {
                        name = cp5.addTextarea(UI_OSC_NAME + key);
                    }
                    name.setPosition(currentX + ui_sys_margin, currentY)
                            .setSize(ui_sys_nameField_width, ui_sys_controlHeight)
                            .setText(key)
                            .setFont(arial14);
                    currentX += ui_sys_margin + ui_sys_nameField_width;

                    Textlabel label = cp5.get(Textlabel.class, UI_OSC_FREQ_LABEL + key);
                    if (null == label) {
                        label = cp5.addLabel(UI_OSC_FREQ_LABEL + key);
                    }
                    label.setPosition(currentX + ui_sys_margin, currentY)
                            .setSize(ui_sys_labelWidth, ui_sys_controlHeight)
                            .setText("frequency");
                    currentX += ui_sys_labelWidth + ui_sys_margin;

                    Textfield value = cp5.get(Textfield.class, UI_OSC_FREQ_VAL + key);
                    if (null == value) {
                        value = cp5.addTextfield(UI_OSC_FREQ_VAL + key);
                    }
                    value.setPosition(currentX + ui_sys_margin, currentY)
                            .setSize(ui_sys_textFieldWidth, ui_sys_controlHeight)
                            .setText(String.valueOf(ui_oscillators.get(key).getFrequency()))
                            .setFont(arial14);
                    currentX += ui_sys_margin + ui_sys_textFieldWidth;

                    currentY += ui_sys_margin + ui_sys_controlHeight;


                }
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
                String varName = c.getName().substring(UI_TOGGLE.length());
                setGroupVisibility(Optional.of(ui_controlGroups.get(varName + "-true")), ((Toggle) c).getBooleanValue());
                setGroupVisibility(Optional.of(ui_controlGroups.get(varName + "-false")), ! ((Toggle) c).getBooleanValue());
                oncePerFrameMethods.remove(varName);
            } else if (controlEvent.getController() instanceof ScrollableList) {
                if ( c.getName().startsWith(UI_OSCIL_SELECTOR)) {
                    if (! ((ScrollableList)c).getItem((int)c.getValue()).get("name").equals(NEW_OSCILLATOR) ) {
                        // this is honestly the best way I have found to get this :-/
                        Oscillator oscillator = ui_oscillators.get(((ScrollableList) c).getItem((int) c.getValue()).get("name"));
                        if ( null != oscillator ) {
                            // this means a variable is being requested to associate with an oscillator
                            Passageways parent = this;

                            String variableName = c.getName().substring(UI_OSCIL_SELECTOR.length());
                            Callable<Optional<List<StateEngine.StateChange>>> varSetterCallable
                                    = new Callable<Optional<List<StateEngine.StateChange>>>() {

                                ValueSetter.ValueSetterHelper helper = (o,f) -> {

                                    //float low = cp5.get(UI_VAL_FROM + variableName).getValue();
                                    float low = Float.valueOf(((Textfield)cp5.get(UI_VAL_FROM + variableName)).getText());
                                    float high = Float.valueOf(((Textfield)cp5.get(UI_VAL_TO + variableName)).getText());

                                    Float value =  ((high - low) / 2f) + (high - low) * oscillator.getValue();
                                    System.out.println( "time=" + System.currentTimeMillis() + ", osc value=" + oscillator.getValue());
                                    try{
                                        //System.out.println( "before set exitSize = " + exitSize);
                                        //System.out.println( "setting value to " + value.toString());
                                        f.set(o,f.getType().cast(value));
                                        //System.out.println( "after set exitSize = " + exitSize);
                                    } catch (IllegalAccessException iaE) {
                                        f.setAccessible(true);
                                        try {
                                            f.set(o,f.getType().cast(value));
                                        } catch (IllegalAccessException iaE2) {
                                            System.out.println( "&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
                                            iaE2.printStackTrace();
                                        }
                                    }

                                };

                                ValueSetter setter = new ValueSetter(variableName, parent, helper);

                                @Override
                                public Optional<List<StateEngine.StateChange>> call() throws Exception {
                                    setter.setValueForVariable();
                                    return Optional.empty();
                                }
                            };

                            //stateMap.put(variableName, varSetterCallable);
                            oncePerFrameMethods.put(variableName, varSetterCallable);

                        }
                    } else {

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

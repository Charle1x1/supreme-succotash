package com.example.calculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //calculator's main displays
    String for_primary_display = "";
    String for_secondary_display = "";
    TextView screen_prim_display, screen_sec_display;

    char operator = '0';
    boolean full_expression_displayed = false;

    String memo;

    //Maths Engine
    compute engine = new compute();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        screen_prim_display = (TextView) findViewById(R.id.primary_display);
        screen_sec_display = (TextView) findViewById(R.id.secondary_display);

    }


    //One function to handle all button clicks
    public void button_sense(View view) {

        switch (view.getId()) {

            case R.id.menu_drawer:
                //view.getBackground();
                //show menu drawer
                break;

            case R.id.overflow_menu:
                //show over flow menu
                break;

            case R.id.one:
                send_to_primary_display("1");
                break;

            case R.id.two:
                send_to_primary_display("2");
                break;

            case R.id.three:
                send_to_primary_display("3");
                break;

            case R.id.four:
                send_to_primary_display("4");
                break;

            case R.id.five:
                send_to_primary_display("5");
                break;

            case R.id.six:
                send_to_primary_display("6");
                break;

            case R.id.seven:
                send_to_primary_display("7");
                break;

            case R.id.eight:
                send_to_primary_display("8");
                break;

            case R.id.nine:
                send_to_primary_display("9");
                break;

            case R.id.zero:
                send_to_primary_display("0");
                break;


            case R.id.percent:
                if ( for_primary_display.length() > 0 ) {

                    percentage();
                }
                break;

            case R.id.memo:
                //the bracket button is now the new memo
                Button memory = findViewById(R.id.memo);
                memory.setEnabled(true);
                break;

            case R.id.dot:
                if ( screen_prim_display.length() > 0 && !for_primary_display.endsWith(".") ) {

                    if ( !for_primary_display.contains(".") )   {
                        //if the number already contains a decimal place then dont add another one
                        for_primary_display = engine.send_to_Display(for_primary_display, ".");
                        setPrimary_display();
                    }
                }
                break;

            case R.id.equals:
                equals();
                break;

            case R.id.romove_input:
                clear_all();
                break;

            case R.id.clear:
                if (screen_prim_display.length() > 0) {
                    clear_field(0);
                } else {

                    clear_field(1);
                }
                break;

            case R.id.backspace:
                if (screen_prim_display.length() > 0) {

                    for_primary_display = engine.display_edit(for_primary_display, 1);
                    setPrimary_display();
                } else {

                    if ( for_secondary_display.contains(" % of ") ) {
                        //if backspace is operating on the sec_display and it contains the percent expression then clear everything on this display
                        for_secondary_display = "";
                        setSecondary_display();
                    }

                    if (screen_sec_display.length() > 0) {

                        for_secondary_display = engine.display_edit(for_secondary_display, 1);
                        setSecondary_display();
                    }
                }

                break;


            //HElLO OPERATORS, PRESSING AGAIN SHOULD CALCULATE RIGHT-AWAY
            case R.id.division:
                if (for_primary_display.length() > 0 || for_secondary_display.endsWith(Character.toString(operator))) {
                    set_operator('/');
                }
                break;

            case R.id.times:
                if (for_primary_display.length() > 0 || for_secondary_display.endsWith(Character.toString(operator))) {
                    set_operator('x');
                }
                break;

            case R.id.subtract:
                set_operator('-');
                break;

            case R.id.addition:
                set_operator('+');
                break;

        }

    }

    //START OF HELPER METHODS, : (C) CHARLES MPANDUKI October 2018    charle1x1@gmail.com

    //Function manages the operators
    public void set_operator(char operator) {

        //so if sec display contains a value and operator and prim display contains a value as well, givin
        // an oper char has been pressed before the current data has been processed  calculate these
        // fist then set the answer on the sec display plus the new opera
        //THIS.OPERATOR
        //*debug*/System.out.println("state of for_sec display = ("+for_secondary_display+")");
        if ( for_secondary_display.length() > 0 && for_primary_display.length() > 0 )   {

            //calculate these and display the answer on sec display along with the new operator
            for_secondary_display = on_fly_calculate();
            print_operator(operator, 1);
            setSecondary_display();

            for_primary_display = "";
            setPrimary_display();
        }



        if ( for_primary_display.length() < 2 && (String.valueOf(operator).equals("+") || String.valueOf(operator).equals("-")) )   {

            //only if primary display contains a non digit number then check for character else continue the operation
            try {

                if ( !Character.isDigit( for_primary_display.charAt(for_primary_display.length()-1)) )  {

                    set_prim_nege_pose_operator( operator );
                    return;
                }
            }   catch (IndexOutOfBoundsException e)    {

                //if an exception occurs then replace the operator on sec_display with new one and exit,.  aweeesoome!!
                //*debug*/ System.out.println("IndexOutOutOfBound exception caught here : prim_display is empty ");
                remove_sec_display_operator();
                print_operator(operator, 1);
                this.operator = operator;
                return;
            }


        }

        //if the primary input ends with just a period then add a zero after the decimal point
        if ( for_primary_display.endsWith(".") )    {

            send_to_primary_display("0");
        }

        //if the number has been send to the sec display by oper press, then pressing the op again should replace the already printed operator
        if ( screen_sec_display.length() > 0 && screen_prim_display.length() < 1 )  {

            remove_sec_display_operator();
            print_operator(operator, 1);
            this.operator = operator;

            return;
        }

        //Pressing the operator again after a chain maths calculation should continue the maths
        if ( screen_sec_display.length() > 0 && screen_prim_display.length() > 0 )  {

            //*debug*/ System.out.println("Hello im an operator handling calculation chains");

            engine.v1 =  get_valid_number( for_secondary_display );

            //set the new operator respective to this calculation chain
            this.operator = operator;

            //get v2 from prim display
            engine.v2 = (String) screen_prim_display.getText();

            //calculate the above values and display answer in primary display whilst the full expression shows in sec display

            equals();
        }

        //If the display already contains the opeator character then delete it and replace with the new operator
        if ( for_primary_display.equals("+") || for_primary_display.equals("-") )   {

            for_primary_display = String.valueOf(operator);

            setPrimary_display();
        }

        //IF there is no digit input yet on either displays, only allow + and -
        if ( (for_primary_display.isEmpty()) && ( operator == '+' || operator == '-' ) ) {

            //*debug*/System.out.println("hello nege/pose characters");
            //if the input is anything other than the operators then ignore this if statement
            if ( for_primary_display.contains("+") || for_primary_display.contains("-") )   {

                for_primary_display = "";
                send_to_primary_display(String.valueOf(operator));
                return;
            }
        }

        //Move the input from primary display to secondary display then add the Operator
        if ( !for_primary_display.isEmpty())    {

            //if prim contains only the char operator, or invalid data then don't send it over to the secondary one
            if ( !corr_value_format(for_primary_display) )   {

                return;
            }

            //at this point the fullMathsExpression should not be displayed
            full_expression_displayed = false;

            move_to_secondary_display(for_primary_display);

            //data now send to sec_disp so assign the engine.v1 value
            engine.v1 = get_valid_number(for_secondary_display);

            //clear primary display to prepare for new input
            send_to_primary_display("");

            //get the operator value received
            this.operator = operator;

            //finally display the operator to be used on sec_display as well
            print_operator(operator, 1);

        }

    }

    public void percentage ()   {

        /*debug*/System.out.println("percent function : for_primary = "+for_primary_display);
        for_secondary_display = for_primary_display+" % of ";
        setSecondary_display();
        for_primary_display = "";
        setPrimary_display();

    }

    public void setPrimary_display() {

        //so if the full expression is displayed and the answer in primary disp gets deleted then no need to keep the full expression displayed in the sec_dis, clear it as well
        if (full_expression_displayed && for_primary_display.isEmpty() )  {

            for_secondary_display = "";
            setSecondary_display();
        }
        screen_prim_display.setText(for_primary_display);
    }

    public void setSecondary_display() {
        //validate the data format fisrt
        screen_sec_display.setText(for_secondary_display);

        //reset expression_displayed back to false
        full_expression_displayed = false;
    }

    public String get_full_maths_exp() {

        String maths = "";

        maths = maths.concat(for_secondary_display); //this includes the operator
        //clear the sec_display to prepare for the full expression data
        for_secondary_display = "";
        maths = maths.concat(" ");
        maths = maths.concat(engine.v2);
        //maths = maths.concat(" ");

        //*debug*/System.out.println("Result from getFullMathsExp : " + maths);

        full_expression_displayed = true;

        return maths;

    }

    public String get_string_b4_space(String data)   {

        int x = 0;

        char[] new_str = data.toCharArray();

        for (int i=0; i < new_str.length; i++)  {

            if ( new_str[i] == ' ' )    {
                break;
            }

            x ++;
        }

        return (String) data.subSequence( 0, x );
    }

    public String get_valid_number (String data)    {

        char[] str_arr = data.toCharArray();

        String new_str = "";

        for (int i = 0; i < str_arr.length; i++)    {

            if ( Character.isDigit(str_arr[i]) )    {

                new_str = new_str.concat( Character.toString(str_arr[i]) );
            }
            else
                return new_str;
        }

        /*debug*/System.out.println("get_valid_number : input = "+data+" output = "+new_str);
        return new_str;
    }

    public void equals() {

        engine.v2 = for_primary_display;

        //Calculate percentage
        if ( for_secondary_display.contains(" % of ") ) {

            String percent_answer;
            /*debug*/System.out.println( "from sec b4 space : "+get_string_b4_space(for_secondary_display)+ " | on primary : "+for_primary_display);
            try {
                percent_answer = engine.compute( engine.compute( get_string_b4_space(for_secondary_display), '/', "100"), 'x', for_primary_display );
            }   catch (NumberFormatException e) {

                //so this means percent calculation failed because there is nothing on the primary display
                /*debug*/System.out.println( "percent calculation failed because there is nothing on the primary display");
                return;
            }

            move_to_secondary_display(for_primary_display);

            send_to_primary_display(percent_answer);

            //clear for_sec for future operations
            for_secondary_display = "";

            full_expression_displayed = true;

            return;
        }

        /*debug*/ System.out.println("Equals = "+engine.v1+operator+engine.v2);
        /*debug*/System.out.println("full_expre_display = " + full_expression_displayed);

        if (!engine.v1.isEmpty()) {

            if (!engine.v2.isEmpty()) {

                if (this.operator != '0') {

                    move_to_secondary_display( get_full_maths_exp() );
                    setSecondary_display();

                    send_to_primary_display(engine.compute(engine.v1, this.operator, engine.v2));

                    this.full_expression_displayed = true;
                    //immediately set sec display data source empty so that current sec display contents wont be included on the next data received
                    for_secondary_display = "";

                }
            }
        }
    }

    public String on_fly_calculate () {
        //this function runs a private calculation using the values currently on the displays including the operator
        /*debug*/System.out.println("on_fly_calculation is running now");
        char new_operator = for_secondary_display.charAt(for_secondary_display.length() - 1);
        return engine.compute( get_valid_number(for_secondary_display), new_operator, for_primary_display);
    }

    public void clear_field(int field) {

        if (field == 0) {
            for_primary_display = "";
            //if the primary display contains the answer then the full expre on the sec displayed should be cleared too
            if (full_expression_displayed)  {

                for_secondary_display = "";
                setSecondary_display();
            }

            setPrimary_display();

            //when you clear the answer/result also clear the sec_display which will be displaying the full maths expression
            if (full_expression_displayed)  {

                for_secondary_display = "";
                setSecondary_display();
            }

        } else {
            for_secondary_display = "";
            setSecondary_display();
        }
    }

    public void clear_all() {

        engine.v1 = "";
        engine.v2 = "";
        this.operator = 0;
        for_primary_display = "";
        for_secondary_display = "";
        this.full_expression_displayed = false;

        setPrimary_display();
        setSecondary_display();
    }

    public void remove_sec_display_operator()  {

        do {

            for_secondary_display = engine.display_edit(for_secondary_display, 1);
        }
        while
        ( !Character.isDigit( for_secondary_display.charAt( for_secondary_display.length() - 1 ) ) );


        setSecondary_display();
    }

    public void set_prim_nege_pose_operator (char operator)  {

        if ( for_secondary_display.isEmpty() && ( for_primary_display.equals("+") || for_primary_display.equals("-") ) )    {

            for_primary_display = engine.display_edit(for_primary_display, 1);

            for_primary_display = Character.toString(operator);

            setPrimary_display();
        }
        else if (for_primary_display.isEmpty()) {

            for_primary_display = Character.toString(operator);

            setPrimary_display();
        }
    }

    public void send_to_primary_display(String data) {

        //So if the full expression is displayed clear the secondary display and continue number input adding to whats already on prim_display if any.
        if ( full_expression_displayed )    {

            full_expression_displayed = false;

            for_secondary_display = "";
            setSecondary_display();
        }


        for_primary_display = engine.send_to_Display(for_primary_display, data);
        setPrimary_display();
    }

    //Returns true if the received string received dose not contain only the char "+" or "-"
    public boolean corr_value_format (String value)   {

        if ( value.equals("+") )   {

            return false;
        }

        if ( value.equals("-") )    {

            return false;
        }

        return true;
    }

    public void move_to_secondary_display(String data) {

        for_secondary_display = engine.send_to_Display(for_secondary_display, data);
        setSecondary_display();

        engine.v1 = get_valid_number(for_secondary_display);

        for_primary_display = "";
        setPrimary_display();


    }

    public void print_operator(char operator, int display_target) {
        //0 for primary_display and 1 or any for sec display
        if (display_target == 0) {

            //surround the character with spaces then send to target.
            for_primary_display = engine.send_to_Display(for_primary_display, " ");
            for_secondary_display = engine.send_to_Display(for_primary_display, String.valueOf(operator));
            for_primary_display = engine.send_to_Display(for_primary_display, " ");

            //Add another space after the operator but directly to the display/screen. the idea is to ignore the space for future operations
            //screen_sec_display.append("apnd");
            setPrimary_display();

        } else {

            //surround the character with spaces then send to target.
            for_secondary_display = engine.send_to_Display(for_secondary_display, " ");
            for_secondary_display = engine.send_to_Display(for_secondary_display, String.valueOf(operator));
            //for_primary_display = engine.send_to_Display(for_primary_display, " ");

            //Add another space after the operator but directly to the display/screen. the idea is to ignore the space for future operations
            //screen_sec_display.append("apnd");
            setSecondary_display();
        }
    }

    public void set_memo (String m) {
        if (m.length() > 0) {

            //Highlight the memo indicator to Has_Data

            this.memo = m;
        }
        else {

            //Highlight the memo indicator to No_data

            memo = m;
        }

    }
    public String get_memo ()   {

        return memo;
    }

}

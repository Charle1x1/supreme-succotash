package com.example.calculator;


public class compute {

    //@auth Charles Mpanduki (started on 16 Aug 2018)

    //Instance variables
    String v1 = "";
    String v2 = "0"; //Default value for engine.v2 will be 0 so pressing Equals with no input on prim_display will not through error

    //function for basic maths operations
    String compute (String v1, char operator, String v2)  {

        //convert both strings to Double then do the maths, real type will be decided later
        double x1 = Double.parseDouble(v1);
        double x2 = Double.parseDouble(v2);

        //get the values to global vars so the helper method (getType) can access the data.
        this.v1 = v1;
        this.v2 = v2;



        switch (operator)   {
            case '+' : return getType(x1 + x2);
            case '-' : return getType(x1 - x2);
            case 'x' : return getType(x1 * x2);
            case '/' : return getType(x1 / x2, true);
        }

        /*debug*/System.out.println("Error : failed to compute "+this.v1+" "+operator+" "+this.v2);
        return "Error : failed to compute";
    }

    //ceck data type, Helper method for the compute method
    String getType ( double answer )  {

        if ( v1.contains(".") || v2.contains(".") )  {
            return Double.toString(answer);
        }
        else {
            return Integer.toString( (int) answer );
        }

    }

    //a special helper for only division operations
    String getType (double answer, boolean divison) {

        return Double.toString(answer);
    }

    /**this function modifies data from the Display then returns the processed data back to Display.
    @param onScreen : contains string data to be processed.
     @param operation : instruction for processing string:(0)clearing/deleting the string. (1)for removing the last char in onScreen string. */
    String display_edit (String onScreen, int operation)    {

        switch (operation)  {
            case 0 : //0 returns ""
                 return "";

            case 1 : //1 is for backspace
                try {
                    return onScreen.substring( 0, onScreen.length()- 1 );
                } catch (IndexOutOfBoundsException e) {

                    return "ERROR!";
                }
        }

        return "";

    }

    //adding new data from key-press to the calculator's display
    String send_to_Display (String currentData, String new_data)    {

        if ( currentData.length() < 1  )
            return new_data;
        else    {
            //concat new data to current data
            return currentData.concat(new_data);
        }
    }

}

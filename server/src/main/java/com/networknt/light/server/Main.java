package com.networknt.light.server;

/**
 * Created by steve on 01/02/15.
 */
import java.util.regex.*;

class Main
{
    public static void main(String[] args)
    {
        String txt="{\"schema\":{\"type\":\"object\",\"title\":\"Add Access Control\",\"properties\":{\"ruleClass\":{\"title\":\"Rule Class\",\"type\":\"string\",\"format\":\"uiselect\",\"items\":[{\"label\":\"dynamic\",\"value\":{\"category\":\"rule\",\"name\":\"getRuleDropdown\"}}]}}}}";

        String re1=".*?";	// Non-greedy match on filler
        String re2="(\"label\")";
        String re3="(:)";	// Any Single Character 1
        String re4="(\"dynamic\")";	// Double Quote String 2
        String re5="(,)";	// Any Single Character 2

        Pattern p = Pattern.compile(re1+re2+re3+re4+re5,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(txt);
        if (m.find())
        {
            String string1=m.group(1);
            String c1=m.group(2);
            String string2=m.group(3);
            String c2=m.group(4);
            System.out.print("("+string1.toString()+")"+"("+c1.toString()+")"+"("+string2.toString()+")"+"("+c2.toString()+")"+"\n");
        }
    }
}
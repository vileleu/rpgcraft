package fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.menu;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseAction {
	String	input;
	int		number;

	ParseAction(String input) {
		this.input = input;
        this.number = 0;
	}

    public void parse() {
        Matcher matcher = Pattern.compile("(\\d+)$").matcher(input);
        if (matcher.find()) {
            input = input.substring(0, matcher.start());
            try {
                number = Integer.parseInt(matcher.group(1));                
            }
            catch (NumberFormatException e) {
                number = 0;
            }
        }
    }

    public String getResult() {
        return input;
    }

    public int getStart() {
        return number * (Menu.BIG_SLOT - 1);
    }
}

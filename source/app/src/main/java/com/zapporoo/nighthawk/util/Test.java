package com.zapporoo.nighthawk.util;

import com.zapporoo.nighthawk.model.ModMessage;
import com.zapporoo.nighthawk.model.ModMyClub;
import com.zapporoo.nighthawk.model.ModRating;
import com.zapporoo.nighthawk.model.ModUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dare on 7.1.16..
 */
public class Test {

    public static List<ModUser> createDummyPersonalBusiness() {
        ModUser item;
        StringBuilder test = new StringBuilder();
        List<ModUser> items = new ArrayList<>();
        for(int i=0; i < 20; ++i) {
            item = new ModUser();
            item.setName("WOOD TAVERN");
            test.setLength(0);
            test.append("$5 12-inch Pizza");
            test.append("\n");
            test.append("$6 Domastic Pitchers");
            test.append("\n");
            test.append("$2 Stella Artois");
            test.append("\n");
            test.append("$3 Imports & IPA's");
            item.setHotDeals(test.toString());
            items.add(item);
        }
        return items;
    }

    public static List<ModUser> createDummyCheckedInUsers() {
        ModUser item;
        List<ModUser> items = new ArrayList<>();
        for(int i=0; i < 20; ++i) {
            item = new ModUser();
            item.setName("Profile " + String.valueOf(i));
            items.add(item);
        }
        return items;
    }

    public static List<ModMyClub> createDummyClubs() {
        List<ModMyClub> items = new ArrayList<>();
//        for (int i = 0; i < 20; ++i) {
//            items.add(new ModMyClub("Favorite Club's Name", 4.5f, false));
//        }
        return items;
    }

    public static List<ModUser> createDummyFriends() {
        List<ModUser> items = new ArrayList<>();

        ModUser item = new ModUser();
        item.setName("Sonja Leonard");
        items.add(item);

        item = new ModUser();
        item.setName("Brandi Lewis");
        items.add(item);

        item = new ModUser();
        item.setName("Sonja Leonard");
        items.add(item);

        return items;
    }

    public static List<ModRating> createDummy() {
        List<ModRating> items = new ArrayList<>();
        for (int i = 0; i < 20; ++i) {
            items.add(new ModRating());
        }
        return items;
    }

    public static List<ModRating> createDummyReviews() {
        List<ModRating> items = new ArrayList<>();
        return items;
    }

    public static List<ModMessage> createDummyMessages() {
        ModMessage item;
        List<ModMessage> items = new ArrayList<>();
        for(int i=0; i < 10; ++i) {
            item = new ModMessage();
            item.mProfileName = "Anna Lee";
            item.mText = "Lorem ipsum dolor sit amet, consetuer adcng elis...";
            item.mTime = "6:49:12";
            item.received = true;
            items.add(item);

            item = new ModMessage();
            item.mProfileName = "Lynda Marshal";
            item.mText = "Lorem ipsum dolor sit amet, consetuer adcng elis...";
            item.mTime = "6:49:12";
            item.received = false;
            items.add(item);
        }
        return items;
    }
}

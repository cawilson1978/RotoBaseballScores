package com.secondavestudios.rotobaseballscores;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;
import com.secondavestudios.rotobaseballscores.model.Player;
import com.secondavestudios.rotobaseballscores.model.Team;

import java.io.File;

public class dbConfigUtil extends OrmLiteConfigUtil {
    private static final Class<?>[] classes = new Class[] {
            Team.class, Player.class
    };

    public static void main(String[] args) throws Exception {
        writeConfigFile(new File("ormlite_config.txt"), classes);
    }
}
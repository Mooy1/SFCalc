package io.github.seggan.sfcalc;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.cscorelib2.chat.ChatColors;
import me.mrCookieSlime.Slimefun.cscorelib2.updater.GitHubBuildsUpdater;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/*
 * Copyright (C) 2020 Seggan
 * Email: segganew@gmail.com
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
public class SFCalc extends JavaPlugin implements SlimefunAddon {

    private static SFCalc instance;

    protected final Set<String> blacklistedRecipes = new HashSet<>();
    protected final Set<String> blacklistedNames = new HashSet<>();
    protected final List<String> itemsSearched = new ArrayList<>();

    // "Localization"
    protected String headerString;
    protected String headerAmountString;
    protected String amountString;
    protected String neededString;
    protected String noItemString;
    protected String noNumberString;
    protected String tooManyCategoriesString;
    protected String tooManyItemsString;
    protected String notAPlayerString;
    protected String invalidNumberString;

    @Override
    public void onEnable() {
        getLogger().info("SFCalc enabled.");

        saveDefaultConfig();

        // ConfigurationSection exceptionSection = config.getConfigurationSection("special-items");
        // Set<String> keys = exceptionSection.getKeys(false);
        // for (String key : keys) {
        // int amount = exceptionSection.getInt(key + ".amount");
        // String item = exceptionSection.getString(key + ".ingredient");
        // String[] ingredients = new String[amount];
        // for (int n = 0; n < amount; n++) {
        // ingredients[n] = item;
        // }
        // exceptions.put(key, ingredients);
        // }

        // init metrics
        new SFCalcMetrics(this);

        if (getConfig().getBoolean("options.auto-update") && getDescription().getVersion().startsWith("DEV - ")) {
            new GitHubBuildsUpdater(this, getFile(), "Seggan/SFCalc/master").start();
        }

        loadStrings();
        registerCommands();
        getServer().getPluginManager().registerEvents(new CalcHandler(), this);

        blacklistedRecipes.add(RecipeType.ORE_WASHER.getKey().getKey());
        blacklistedRecipes.add(RecipeType.GEO_MINER.getKey().getKey());
        blacklistedRecipes.add(RecipeType.GOLD_PAN.getKey().getKey());
        blacklistedRecipes.add(RecipeType.MOB_DROP.getKey().getKey());
        blacklistedRecipes.add(RecipeType.BARTER_DROP.getKey().getKey());
        blacklistedRecipes.add(RecipeType.ORE_CRUSHER.getKey().getKey());
        blacklistedRecipes.add(RecipeType.NULL.getKey().getKey());
        blacklistedRecipes.add("void_harvester");

        blacklistedNames.add("uu-matter");
        blacklistedNames.add("silicon");

        instance = this;
    }

    private void registerCommands() {
        CalcExecutor executor = new CalcExecutor(this);
        CalcCompleter completer = new CalcCompleter();

        Objects.requireNonNull(getCommand("sfcalc")).setExecutor(executor);
        Objects.requireNonNull(getCommand("sfcalc")).setTabCompleter(completer);

        Objects.requireNonNull(getCommand("sfneeded")).setExecutor(executor);
        Objects.requireNonNull(getCommand("sfneeded")).setTabCompleter(completer);

    }

    @Override
    public void onDisable() {
        getLogger().info("SFCalc disabled.");
    }

    @Override
    public JavaPlugin getJavaPlugin() {
        return this;
    }

    @Override
    public String getBugTrackerURL() {
        return "https://github.com/Seggan/SFCalc/issues";
    }

    static SFCalc getInstance() {
        return instance;
    }

    private void loadStrings() {
        headerString = loadString("header-string", "&e&nRecipe for %s:");
        headerAmountString = loadString("header-amount-string", "&e&nRecipe for %d %s:");
        amountString = loadString("amount-string", "&e%d of %s");
        neededString = loadString("needed-string", "&e%d more %s needed");
        noItemString = loadString("no-item-string", "&cThat item was not found.");
        noNumberString = loadString("no-number-string", "&cThat's not a number!");
        tooManyCategoriesString = loadString("category-error-string", "&cThat many categories is not supported yet. Please use the command form of the calculator.");
        tooManyItemsString = loadString("item-error-string", "&cThat many items is not supported yet. Please use the command form of the calculator.");
        notAPlayerString = loadString("not-a-player-string", "&cYou must be a player to send this message!");
        invalidNumberString = loadString("invalid-number-string", "&cInvalid number!");
    }

    private String loadString(String path, String defaultValue) {
        String value = getConfig().getString(path);

        if (value != null) {
            return ChatColors.color(value);
        } else {
            return ChatColors.color(defaultValue);
        }
    }
}

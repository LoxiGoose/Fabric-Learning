package net.goose.lifesteal.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.goose.lifesteal.LifeSteal;

@Config(name = LifeSteal.MOD_ID)
public class ModConfig implements ConfigData {

    @Comment("This category holds general values that will mostly be customized by most.")
    public StartingConfig StartingConfigurations = new StartingConfig();
    @Comment("This category is the configuration for items and enchantments in this mod")
    public ItemsAndEnchantments ItemsAndEnchantments = new ItemsAndEnchantments();
    @Comment("This category is everything related to life stealing from someone.")
    public LifestealRelated LifestealRelated = new LifestealRelated();
    @Comment("This category will hold the maximums for certain values")
    public Maximums Maximums = new Maximums();
    @Comment("This category holds values that aren't intended for gameplay and aren't polished, but can be used to test certain aspects of the mod easily or just for good fun.")
    public MISCANDFUN MiscandFun = new MISCANDFUN();

    public static class StartingConfig {
        @Comment("This value modifies how many hearts you'll start at in a world. 2 would mean 1 extra heart, -2 would mean 1 less heart. If you have lives enabled, you'll gain a life when you get max hearts double your starting hearts. EX: If 3 hearts is your starting value, you'll gain a life if you get 3 more hearts. The range must be above -19; The default is 0")
        public int StartingHitPointDifference = 0;
        @Comment("When this is true, you will lose hearts when killed by a player. Otherwise, you can lose max hearts just by any sorts of death.. (This is overridden by the mob value below if it's true); The default is false")
        public boolean LoseHeartsOnlyWhenKilledByPlayer = false;
        @Comment("When this is true, you will lose hearts when killed by a mob. Otherwise, you can lose max hearts just by any sorts of death; The default is false")
        public boolean LoseHeartsOnlyWhenKilledByMob = false;
        @Comment("This values modifies the amount of hit points that should be lost when you die. The same also applies when you gain max health from lifestealing. 2 hit points = 1 health; The default is 2")
        public int AmountOfHitPointsGivenorTaken = 2;
        @Comment("This value determines if a PLAYER should lose HEARTS AT ALL; The default is false")
        public boolean DisableHeartLoss = false;
        @Comment("When this is false, players that lose all lives/hearts will go into spectator mode. Otherwise, they'll be banned until unbanned; The default is true")
        public boolean BannedUponLosingAllHearts = true;
    }

    public static class ItemsAndEnchantments{
        @Comment("This is the amount of hit points a Heart Crystal should give when used. 2 HitPoints = 1 Heart, 3 = 1.5 Heart; The range must be greater than 1; The default is 2")
        public int AmountOfHitPointsCrystalGives = 2;
        @Comment("The percentage of max health a heart core should heal when used;Range must be 0.01~1.0; Default is 0.25")
        public double PercentageHeartCoreHeals = 0.25;
        @Comment("If you just want the generic Lifesteal mod, you can disable this and nobody can gain hearts through Heart Crystals but only through lifestealing; Default is false")
        public boolean DisableHeartCrystals = false;
        @Comment("This value determines if heart cores are disabled or not; Default is false")
        public boolean DisableHeartCores = false;
    }

    public static class LifestealRelated{
        @Comment("This option changes the entire mod into more of a permanent heart gaining system. This makes it so nobody can gain hearts from lifestealing but ONLY through Heart Crystals. MOBS can still take your hearts away if they kill you though, UNLESS you have that option disabled; Default is false")
        public boolean DisableLifesteal = false;
        @Comment("This value determines if a player should still earn hearts from a player they killed even if the player doesn't have hearts to spare. EX: MaximumHeartHave; Default is false")
        public boolean PlayersGainHeartsFromNoHeartPlayers = false;
    }

    public static class Maximums{
        @Comment("This value makes a limit SET after your Starting HitPoint Difference for how many hit points/hearts a player can get. 2 hit points = 1 heart. Set this to less than 1 to disable the feature; Default is -1")
        public int MaximumHitPointsPlayerCanGet = -1;
        @Comment("This value makes a limit set on how many hit points/hearts a player can lose, this value is actually set depending on the Starting Health Difference. EX: Starting Health Difference - MinimumHeartHave. Set this to less than 0 to disable the feature; Default is -1")
        public int MaximumHitPointsPlayerCanLose = -1;
    }

    public static class MISCANDFUN{
        @Comment("When this is false, you can only gain hearts from killing players. Otherwise, any mob will give you hearts; Default is false")
        public boolean KillingAnyMobsGivesYouHearts = false;
    }

    public static void register(){
        LifeSteal.LOGGER.debug("Registering ModConfig for "+LifeSteal.MOD_ID);
        AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
    }
}

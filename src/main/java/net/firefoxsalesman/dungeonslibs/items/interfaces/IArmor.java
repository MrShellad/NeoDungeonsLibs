package net.firefoxsalesman.dungeonslibs.items.interfaces;

public interface IArmor {

	boolean isUnique();

	default boolean doGivesYouAPetBat() {
		return false;
	}

	default double getFreezingResistance() {
		return 0;
	}
}

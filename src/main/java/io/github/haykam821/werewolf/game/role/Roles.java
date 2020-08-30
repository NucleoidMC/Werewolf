package io.github.haykam821.werewolf.game.role;

import io.github.haykam821.werewolf.Main;
import net.minecraft.util.Identifier;

public enum Roles {
	VILLAGER("villager", new VillagerRole()),
	SEER("seer", new SeerRole()),
	WOLF("wolf", new WolfRole());

	private Role role;

	private Roles(String path, Role role) {
		this.role = role;

		Identifier id = new Identifier(Main.MOD_ID, path);
		Role.REGISTRY.register(id, role);
	}

	public Role getRole() {
		return this.role;
	}

	public static void initialize() {
		return;
	}
}
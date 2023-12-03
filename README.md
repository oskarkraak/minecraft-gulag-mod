# Minecraft Gulag Mod

This is a Minecraft server-side mod that spawns players in a new world each time they die.
They can get back to their home world by defeating the Ender Dragon in the new world.

## Why?

This mod aims to mimic the feel of Minecraft's Hardcore difficulty without the risk of permanently losing the world.
Instead, the players get punished by having to defeat the ender dragon in a new world. This will improve their skill
level and speedrun abilities.

## The Name

The mod is named after the "gulag" from Call of Duty, where players get a second chance if they defeat an opponent in
duel.
In a similar fashion, this mod gives the player a second chance by letting them duel the Ender Dragon instead of
deleting the world.
The only difference is that they will simply respawn in their new world if they die there.

## How does it work?

When a player dies, the gulag will be created with a random seed.
The player's inventory will be deleted, and they will be spawned into the gulag.
In multiplayer, all players who die afterwards will be brought to the same gulag.

The gulag consists of the Overworld, the Nether, and the End.
In the gulag, the player must play through the game as usual with the ultimate goal of defeating the ender dragon.
If a player dies in the gulag, their items will drop, and they will respawn in the gulag.

Upon defeating the ender dragon, the end portal will appear which will transport the player back to the spawn point (the
server spawn point) of their original world.
The player will keep all items acquired in the gulag.
As soon as everyone has left the gulag, it will be deleted.
Now, if a player dies, a new gulag will be created with a new random seed.

## Install

This is a fabric mod.
Since it only accesses server resources, it can be installed in the client for single-player or on a server for
multiplayer. If installed on a server, the players **don't** need to install anything.

1. Choose a Minecraft version you want to play.
   Note that the gulag mod is not available for all versions of Minecraft.
   You can find all gulag mod versions [here](https://github.com/oskarkraak/minecraft-gulag-mod/releases): The first
   version number denotes the gulag mod version, the second the Minecraft version (i.e. v1.0.0+1.19.4 is version 1.0.0
   of the gulag mod for Minecraft version 1.19.4).
   Ideally you choose a Minecraft version for which the latest version of the gulag mod is available.
1. Download and start the [Fabric installer](https://fabricmc.net/use/installer/).
1. Choose your Minecraft version and press "Install".
1. Download the correct version of each of the following into the `mods` folder of your Minecraft installation:
   1. [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api)
   1. [Fantasy library](https://github.com/NucleoidMC/fantasy/releases)
   1. [Gulag mod](https://github.com/oskarkraak/minecraft-gulag-mod/releases)

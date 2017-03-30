package obsidianAPI.animation;
//package com.nthrootsoftware.mcea.animation;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//
//import com.google.common.collect.Maps;
//import com.nthrootsoftware.mcea.render.objRendering.ModelObj;
//
//import net.minecraft.block.Block;
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemStack;
//import net.minecraft.nbt.NBTTagCompound;
//import net.minecraft.nbt.NBTTagList;
//
///*
// * Contains all the data for animations and models.
// * 
// * Also holds information regarding the setup of GUIs (zoom, rotation, position etc.)
// */
//
//public class AnimationData 
//{	
//	
//	//Setup for GUIs
//	private static Map<String, String> guiSetup = Maps.newHashMap();
//
//	public static void setGUISetup(String entityName, String setup)
//	{
//		if(!setup.equals(""))
//		{
//			guiSetup.put(entityName, setup);
//		}
//	}
//
//	public static String getGUISetup(String entityName)
//	{
//		return guiSetup.get(entityName);
//	}
//
//	public static NBTTagCompound getGUISetupTag(List<String> entities)
//	{
//		NBTTagCompound nbt = new NBTTagCompound();
//		NBTTagList entityList = new NBTTagList();
//		for(String entity : entities)
//		{ 
//			NBTTagCompound guiSetupCompound = new NBTTagCompound();
//			guiSetupCompound.setString("EntityName", entity);
//			String setup = guiSetup.get(entity);
//			if(setup != null && !setup.equals(""))
//				guiSetupCompound.setString("GUISetup", setup);
//			entityList.appendTag(guiSetupCompound);
//		}
//		nbt.setTag("GuiSetup", entityList);
//
//		NBTTagList animationItemList = new NBTTagList();
//		for(Entry<String, Integer> e : animationItems.entrySet())
//		{
//			NBTTagCompound animationItem = new NBTTagCompound();
//			animationItem.setString("name", e.getKey());
//			animationItem.setInteger("id", e.getValue());
//			animationItemList.appendTag(animationItem);
//		}
//		nbt.setTag("AnimationItems", animationItemList);
//
//		return nbt;
//	}	
//
//	public static void loadGUISetup(NBTTagCompound nbt)
//	{
//		System.out.println("Loading gui setup...");
//		NBTTagList entityList = nbt.getTagList("GuiSetup", 10);
//		for(int i = 0; i < entityList.tagCount(); i++)
//		{
//			NBTTagCompound guiSetupCompound = entityList.getCompoundTagAt(i);
//			String entityName = guiSetupCompound.getString("EntityName");
//			setGUISetup(entityName, guiSetupCompound.getString("GUISetup"));
//		}
//
//		NBTTagList animationItemList = nbt.getTagList("AnimationItems", 10);
//		for(int i = 0; i < animationItemList.tagCount(); i++)
//		{
//			NBTTagCompound animationItem = animationItemList.getCompoundTagAt(i);
//			setAnimationItem(animationItem.getString("name"), animationItem.getInteger("id"));
//		}
//
//		System.out.println(" Done");
//	}	
//
//	
//
//}

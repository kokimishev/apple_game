package controllers;

import java.util.List;

import models.Base;
import models.Farmer;
import models.Field;
import models.PlantType;
import models.Plantation;
import models.Seedling;
import models.SeedlingType;
import models.Terrain;
import models.TerrainAnalysis;
import play.mvc.Controller;

public class TerrainShop extends Controller {

	public static void allTerrains() throws Exception {
		JsonController.toJson(Terrain.findAll(), "analysis");
	}

	public static void analyze(Long terrainId) throws Exception {
		Terrain t = Terrain.findById(terrainId);
		Farmer farmer = AuthController.getFarmer();
		farmer.balans -= TerrainAnalysis.ANALYSIS_PRICE;
		if (farmer.balans > 0) {
			farmer.save();
		}
		JsonController.toJson(t.analysis.features, "category");
	}

	public static void buyTerrain(Long terrainId, Double size,
			String currentState) throws Exception {

		Field field = new Field();
		field.area = size;
		field.terrain = Terrain.findById(terrainId);

		Farmer farmer = AuthController.getFarmer();
		farmer.currentState = currentState;
		farmer.field = field;
		farmer.balans -= field.terrain.analysis.unitPrice * size;

		if (farmer.balans > 0) {
			field.save();
			farmer.save();
		}
		JsonController.toJson(farmer, "field");
	}

	public static void allBases() throws Exception {
		JsonController.toJson(Base.findAll());
	}

	public static void buyBase(Long itemid, String currentState)
			throws Exception {
		Farmer farmer = AuthController.getFarmer();
		Field field = Field.find("owner.id", farmer.id).first();

		Plantation plantation = new Plantation();
		plantation.base = Base.findById(itemid);

		field.plantation = plantation;

		farmer.currentState = currentState;
		farmer.balans -= plantation.base.price;

		if (farmer.balans > 0) {
			plantation.save();
			field.save();
			farmer.save();
		}
		JsonController.toJson(farmer, "field", "plantation");
	}

	public static void allSeedlings() throws Exception {
		JsonController.toJson(Seedling.findAll(), "seedlingType", "type");
	}

	public static void buySeedling(Long seedlingTypeId, Long plantTypeId,
			String currentState) throws Exception {
		Farmer farmer = AuthController.getFarmer();

		Plantation plantation = Plantation.find("field.owner.id", farmer.id)
				.first();

		plantation.seadlings = Seedling.find("type.id=:t and seedlingType.id=:st")
				.setParameter("t", plantTypeId)
				.setParameter("st", seedlingTypeId).first();

		farmer.currentState = currentState;
		farmer.balans -= plantation.seadlings.price * plantation.field.area;

		if (farmer.balans > 0) {
			plantation.save();
			farmer.save();
		}
		JsonController.toJson(farmer, "seedlingType", "type");
	}

	public static void generateAllSeedlings() {
		List<SeedlingType> stList = SeedlingType.findAll();
		List<PlantType> ptList = PlantType.findAll();
		for (SeedlingType st : stList) {
			for (PlantType pt : ptList) {
				Seedling s = new Seedling();
				s.seedlingType = st;
				s.type = pt;
				s.price = 1;
				s.save();
			}
		}
	}

}

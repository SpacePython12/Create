package com.simibubi.create.compat.recipeViewerCommon;

import java.util.function.Supplier;

import com.simibubi.create.compat.emi.EmiSequencedAssemblySubCategory;
import com.simibubi.create.compat.jei.category.sequencedAssembly.JeiSequencedAssemblySubCategory;
import com.simibubi.create.compat.rei.category.sequencedAssembly.ReiSequencedAssemblySubCategory;

public record SequencedAssemblySubCategoryType(Supplier<Supplier<JeiSequencedAssemblySubCategory>> jei,
											   Supplier<Supplier<ReiSequencedAssemblySubCategory>> rei,
											   Supplier<Supplier<EmiSequencedAssemblySubCategory>> emi) {

	public static final SequencedAssemblySubCategoryType PRESSING = new SequencedAssemblySubCategoryType(
			() -> JeiSequencedAssemblySubCategory.AssemblyPressing::new,
			() -> ReiSequencedAssemblySubCategory.AssemblyPressing::new,
			() -> EmiSequencedAssemblySubCategory.AssemblyPressing::new
	);
	public static final SequencedAssemblySubCategoryType SPOUTING = new SequencedAssemblySubCategoryType(
			() -> JeiSequencedAssemblySubCategory.AssemblySpouting::new,
			() -> ReiSequencedAssemblySubCategory.AssemblySpouting::new,
			() -> EmiSequencedAssemblySubCategory.AssemblySpouting::new
	);
	public static final SequencedAssemblySubCategoryType DEPLOYING = new SequencedAssemblySubCategoryType(
			() -> JeiSequencedAssemblySubCategory.AssemblyDeploying::new,
			() -> ReiSequencedAssemblySubCategory.AssemblyDeploying::new,
			() -> EmiSequencedAssemblySubCategory.AssemblyDeploying::new
	);
	public static final SequencedAssemblySubCategoryType CUTTING = new SequencedAssemblySubCategoryType(
			() -> JeiSequencedAssemblySubCategory.AssemblyCutting::new,
			() -> ReiSequencedAssemblySubCategory.AssemblyCutting::new,
			() -> EmiSequencedAssemblySubCategory.AssemblyCutting::new
	);
}

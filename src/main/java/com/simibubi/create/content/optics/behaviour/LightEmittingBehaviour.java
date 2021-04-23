package com.simibubi.create.content.optics.behaviour;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.common.collect.Iterators;
import com.simibubi.create.content.optics.Beam;
import com.simibubi.create.content.optics.ILightHandler;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.behaviour.BehaviourType;

import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;

public class LightEmittingBehaviour<T extends SmartTileEntity & ILightHandler.ILightHandlerProvider> extends AbstractLightHandlingBehaviour<T> {
	public static final BehaviourType<LightEmittingBehaviour<? extends SmartTileEntity>> TYPE = new BehaviourType<>();

	boolean updating = false;


	public LightEmittingBehaviour(T te, LightHandlingBehaviourProperties properties) {
		super(te, properties);
	}

	public LightEmittingBehaviour(T te) {
		super(te, LightHandlingBehaviourProperties.create()
				.withScansBeacons(false)
				.withAbsorbsLight(true));
	}

	@Override
	public void updateBeams() {
		super.updateBeams();
		if (updating)
			return;
		updating = true;

		beams.forEach(Beam::onRemoved);
		beams.clear();
		getOutBeamDirections().map(this::constructOutBeam)
				.filter(Objects::nonNull)
				.filter(((Predicate<Beam>) Beam::isEmpty).negate())
				.peek(beams::add)
				.forEach(Beam::onCreated);

		updating = false;
	}

	@Override
	public BehaviourType<?> getType() {
		return TYPE;
	}

	public Stream<Vector3d> getOutBeamDirections() {
		return Stream.of(Vector3d.of(tileEntity.getBlockState()
				.method_28500(BlockStateProperties.FACING)
				.orElse(Direction.UP)
				.getDirectionVec()));
	}

	@Override
	public Iterator<Beam> getRenderBeams() {
		return Iterators.concat(beams.iterator(), super.getRenderBeams());
	}
}

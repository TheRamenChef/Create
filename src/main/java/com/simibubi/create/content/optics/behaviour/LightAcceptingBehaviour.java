package com.simibubi.create.content.optics.behaviour;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.simibubi.create.content.optics.Beam;
import com.simibubi.create.content.optics.ILightHandler;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.behaviour.BehaviourType;

public class LightAcceptingBehaviour<T extends SmartTileEntity & ILightHandler.ILightHandlerProvider> extends AbstractLightHandlingBehaviour<T> {
	public static final BehaviourType<LightAcceptingBehaviour<? extends SmartTileEntity>> TYPE = new BehaviourType<>();
	boolean isUpdating = false;

	public LightAcceptingBehaviour(T te, LightHandlingBehaviourProperties properties) {
		super(te, properties);
	}

	public LightAcceptingBehaviour(T te) {
		super(te, LightHandlingBehaviourProperties.create()
				.withAbsorbsLight(true)
				.withScansBeacons(true));
	}

	@Override
	public void updateBeams() {
		super.updateBeams();
		if (isUpdating)
			return;
		isUpdating = true;

		beams = beams.stream()
				.filter(Objects::nonNull)
				.filter(((Predicate<Beam>) Beam::isRemoved).negate())
				.collect(Collectors.toSet());
		isUpdating = false;
	}

	@Override
	public BehaviourType<?> getType() {
		return TYPE;
	}

	@Override
	public Stream<Beam> constructSubBeams(Beam beam) {
		if (beams.stream()
				.map(Beam::getParent)
				.filter(Objects::nonNull)
				.filter(((Predicate<Beam>) Beam::isRemoved).negate())
				.map(Beam::getDirection)
				.filter(Objects::nonNull)
				.noneMatch(b -> b.equals(beam.getDirection())))
			beams.add(beam);
		return Stream.empty();
	}
}

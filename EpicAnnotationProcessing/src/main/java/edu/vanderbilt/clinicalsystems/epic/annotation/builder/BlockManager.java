package edu.vanderbilt.clinicalsystems.epic.annotation.builder;

import edu.vanderbilt.clinicalsystems.m.lang.model.Block;
import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineElement;

public class BlockManager extends RoutineElementsManager {

	private final Block m_block ;
	
	public BlockManager( Block block ) {
		m_block = block ;
	}
	
	@Override
	protected void applyElement(RoutineElement element) {
		m_block.appendElement(element);
	}

}

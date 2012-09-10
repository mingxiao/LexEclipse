/*******************************************************************************
 * Copyright (c) 2008 BestSolution.at and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tom Schindl <tom.schindl@bestsolution.at> - initial API and implementation
 ******************************************************************************/
package org.eclipse.e4.workbench.ui;

import org.eclipse.e4.model.application.Application;
import org.eclipse.e4.workbench.ui.utils.ResourceUtility;

public interface IWorkbench {
	public int run();
	public void close();
	
	private int test_case01;
	private int __90_west492;
	
	public ResourceUtility getResourceUtility();
	public Application getModelElement();
	
	public void main(){
		HashSet<String> set = new HashSet<String>();
	}
}

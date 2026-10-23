// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.cannery.actions;

import com.hbm.wiaj.JarScene;
import com.hbm.wiaj.WorldInAJar;
import com.hbm.wiaj.actions.ActionOffsetBy;
import com.hbm.wiaj.actions.ActionSetZoom;

/** Runs the existing scene offset and zoom actions on the same ticks. */
public class ActionOffsetAndZoom extends ActionOffsetBy {
    private final ActionSetZoom zoomAction;

    public ActionOffsetAndZoom(double x, double y, double z, double zoom, int time) {
        super(x, y, z, time);
        zoomAction = new ActionSetZoom(zoom, time);
    }

    @Override
    public void act(WorldInAJar world, JarScene scene) {
        super.act(world, scene);
        zoomAction.act(world, scene);
    }
}

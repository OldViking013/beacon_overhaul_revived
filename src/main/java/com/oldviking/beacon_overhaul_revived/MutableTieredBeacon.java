package com.oldviking.beacon_overhaul_revived;

import com.oldviking.beacon_overhaul_revived.tags.PotencyTier;

public interface MutableTieredBeacon extends TieredBeacon {
    void setTier(final PotencyTier tier);
}

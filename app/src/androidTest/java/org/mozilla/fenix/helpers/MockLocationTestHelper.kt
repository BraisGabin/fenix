package org.mozilla.fenix.helpers

import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.SystemClock
import androidx.test.platform.app.InstrumentationRegistry

object MockLocationTestHelper {
    fun mockLocation() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val lm = context.getSystemService(
            Context.LOCATION_SERVICE
        ) as LocationManager
        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_FINE
        val mocLocationProvider =
            LocationManager.GPS_PROVIDER //lm.getBestProvider( criteria, true );
        lm.addTestProvider(
            mocLocationProvider, false, false,
            false, false, false, false, false, 0, 5
        )
        lm.setTestProviderEnabled(mocLocationProvider, true)
        val loc = Location(mocLocationProvider)
        val mockLocation = Location(mocLocationProvider) // a string
        mockLocation.latitude = -26.902038 // double
        mockLocation.longitude = -48.671337
        mockLocation.altitude = loc.altitude
        mockLocation.time = System.currentTimeMillis()
        mockLocation.accuracy = 1f
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mockLocation.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
        }
        lm.setTestProviderLocation(mocLocationProvider, mockLocation)
    }
}
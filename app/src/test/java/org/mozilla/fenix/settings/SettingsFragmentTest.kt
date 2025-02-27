/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.settings

import androidx.fragment.app.FragmentActivity
import androidx.preference.Preference
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.test.TestCoroutineDispatcher
import mozilla.components.concept.fetch.Client
import mozilla.components.support.test.robolectric.testContext
import mozilla.components.support.test.rule.MainCoroutineRule
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mozilla.fenix.Config
import org.mozilla.fenix.R
import org.mozilla.fenix.ReleaseChannel
import org.mozilla.fenix.ext.components
import org.mozilla.fenix.ext.getPreferenceKey
import org.mozilla.fenix.helpers.FenixRobolectricTestRunner
import org.mozilla.fenix.utils.Settings
import org.robolectric.Robolectric
import java.io.IOException

@RunWith(FenixRobolectricTestRunner::class)
class SettingsFragmentTest {

    private val testDispatcher = TestCoroutineDispatcher()

    @get:Rule
    val coroutinesTestRule = MainCoroutineRule(testDispatcher)

    @Before
    fun setup() {
        // Mock client for fetching account avatar
        val client = mockk<Client>()
        every { client.fetch(any()) } throws IOException("test")

        every { testContext.components.core.client } returns client
        every { testContext.components.settings } returns mockk(relaxed = true)
        every { testContext.components.analytics } returns mockk(relaxed = true)
        every { testContext.components.backgroundServices } returns mockk(relaxed = true)

        mockkObject(Config)
        every { Config.channel } returns ReleaseChannel.Nightly
    }

    @After
    fun cleanUp() {
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `Add-on collection override pref is visible if debug menu active`() {
        val settingsFragment = SettingsFragment()
        val activity = Robolectric.buildActivity(FragmentActivity::class.java).create().get()

        activity.supportFragmentManager.beginTransaction()
            .add(settingsFragment, "test")
            .commitNow()

        testDispatcher.advanceUntilIdle()

        val preferenceAmoCollectionOverride = settingsFragment.findPreference<Preference>(
            settingsFragment.getPreferenceKey(R.string.pref_key_override_amo_collection)
        )

        settingsFragment.setupAmoCollectionOverridePreference(mockk(relaxed = true))
        assertNotNull(preferenceAmoCollectionOverride)
        assertFalse(preferenceAmoCollectionOverride!!.isVisible)

        val settings: Settings = mockk(relaxed = true)
        every { settings.showSecretDebugMenuThisSession } returns true
        settingsFragment.setupAmoCollectionOverridePreference(settings)
        assertTrue(preferenceAmoCollectionOverride.isVisible)
    }

    @Test
    fun `Add-on collection override pref is visible if already configured`() {
        val settingsFragment = SettingsFragment()
        val activity = Robolectric.buildActivity(FragmentActivity::class.java).create().get()

        activity.supportFragmentManager.beginTransaction()
            .add(settingsFragment, "test")
            .commitNow()

        testDispatcher.advanceUntilIdle()

        val preferenceAmoCollectionOverride = settingsFragment.findPreference<Preference>(
            settingsFragment.getPreferenceKey(R.string.pref_key_override_amo_collection)
        )

        settingsFragment.setupAmoCollectionOverridePreference(mockk(relaxed = true))
        assertNotNull(preferenceAmoCollectionOverride)
        assertFalse(preferenceAmoCollectionOverride!!.isVisible)

        val settings: Settings = mockk(relaxed = true)
        every { settings.showSecretDebugMenuThisSession } returns false

        every { settings.amoCollectionOverrideConfigured() } returns false
        settingsFragment.setupAmoCollectionOverridePreference(settings)
        assertFalse(preferenceAmoCollectionOverride.isVisible)

        every { settings.amoCollectionOverrideConfigured() } returns true
        settingsFragment.setupAmoCollectionOverridePreference(settings)
        assertTrue(preferenceAmoCollectionOverride.isVisible)
    }
}

package com.sumas.wooheso.core.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseConfig {
    // Configurable Supabase credentials
    var supabaseUrl: String = "https://cdfymiolrbkfnmdqtojn.supabase.co"
    var supabaseAnonKey: String = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImNkZnltaW9scmJrZm5tZHF0b2puIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODc0NjYxNTgsImV4cCI6MjEwMzA0MjE1OH0._xyvkmUTBkPtmJNiKOY7zW3sooOyI1Ugyux3Z6PaukM"

    val isConfigured: Boolean
        get() = supabaseUrl.startsWith("https://") && !supabaseUrl.contains("your-project")
}

object SupabaseClientProvider {
    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = SupabaseConfig.supabaseUrl,
            supabaseKey = SupabaseConfig.supabaseAnonKey
        ) {
            install(Auth)
            install(Postgrest)
            install(Storage)
        }
    }
}

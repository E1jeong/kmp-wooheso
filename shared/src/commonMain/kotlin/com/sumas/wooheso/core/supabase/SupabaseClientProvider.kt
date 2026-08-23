package com.sumas.wooheso.core.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseConfig {
    // Configurable Supabase credentials
    var supabaseUrl: String = "https://your-project.supabase.co"
    var supabaseAnonKey: String = "your-anon-key"

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

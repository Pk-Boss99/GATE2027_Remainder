package com.pushk.gatewallpaper

import android.app.AlertDialog
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object UpdateManager {
    private val scope = MainScope()

    fun checkForUpdates(context: Context) {
        Toast.makeText(context, "Checking GitHub for updates...", Toast.LENGTH_SHORT).show()
        
        scope.launch {
            val updateInfo = fetchLatestRelease()

            if (updateInfo != null) {
                val (latestVersion, apkUrl) = updateInfo
                var currentVersion = context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "1.0"
                if (!currentVersion.startsWith("v")) currentVersion = "v$currentVersion"
                
                if (latestVersion != currentVersion && latestVersion.isNotEmpty()) {
                    showUpdateDialog(context, latestVersion, apkUrl)
                } else {
                    Toast.makeText(context, "You are on the latest version!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Failed to check for updates.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun fetchLatestRelease(): Pair<String, String>? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("https://api.github.com/repos/Pk-Boss99/GATE2027_Remainder/releases/latest")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("User-Agent", "GATE-Wallpaper-App")

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val jsonResponse = reader.readText()
                    reader.close()

                    val json = JSONObject(jsonResponse)
                    val tagName = json.getString("tag_name")
                    val assets = json.getJSONArray("assets")
                    
                    var apkUrl = ""
                    for (i in 0 until assets.length()) {
                        val asset = assets.getJSONObject(i)
                        if (asset.getString("name").endsWith(".apk")) {
                            apkUrl = asset.getString("browser_download_url")
                            break
                        }
                    }

                    if (apkUrl.isNotEmpty()) {
                        return@withContext Pair(tagName, apkUrl)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            null
        }
    }

    private fun showUpdateDialog(context: Context, newVersion: String, apkUrl: String) {
        AlertDialog.Builder(context)
            .setTitle("Update Available!")
            .setMessage("Version $newVersion is available. Do you want to download and install it now?")
            .setPositiveButton("Update") { _, _ ->
                downloadAndInstall(context, apkUrl)
            }
            .setNegativeButton("Later", null)
            .show()
    }

    private fun downloadAndInstall(context: Context, apkUrl: String) {
        val request = DownloadManager.Request(Uri.parse(apkUrl))
        request.setTitle("Downloading Update")
        request.setDescription("Downloading latest version of GATE Wallpaper...")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        
        val fileName = "app-update.apk"
        val destination = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
        if (destination.exists()) destination.delete()
        
        request.setDestinationUri(Uri.fromFile(destination))

        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = manager.enqueue(request)

        Toast.makeText(context, "Download started...", Toast.LENGTH_SHORT).show()

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context, intent: Intent) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id == downloadId) {
                    context.unregisterReceiver(this)
                    installApk(context, destination)
                }
            }
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_EXPORTED)
        } else {
            context.registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        }
    }

    private fun installApk(context: Context, apkFile: File) {
        try {
            val uri = FileProvider.getUriForFile(context, "com.pushk.gatewallpaper.fileprovider", apkFile)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "application/vnd.android.package-archive")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to start installation.", Toast.LENGTH_LONG).show()
        }
    }
}

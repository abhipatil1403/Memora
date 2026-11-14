package com.memora.core.ui.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import android.widget.Toast

object MemoraIntents {

    fun dialPhone(context: Context, phoneNumber: String) {
        try {
            val cleanNumber = phoneNumber.filter { it.isDigit() || it == '+' }
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$cleanNumber"))
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "Could not open phone dialer", Toast.LENGTH_SHORT).show()
        }
    }

    fun openUrl(context: Context, urlOrMeeting: String) {
        try {
            val extractedUrl = Regex("https?://[^\\s)]+", RegexOption.IGNORE_CASE).find(urlOrMeeting)?.value ?: urlOrMeeting
            val safeUrl = if (extractedUrl.startsWith("http://") || extractedUrl.startsWith("https://")) {
                extractedUrl
            } else {
                "https://$extractedUrl"
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(safeUrl))
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "Could not open link", Toast.LENGTH_SHORT).show()
        }
    }

    fun openMaps(context: Context, locationQuery: String) {
        try {
            val encodedLocation = Uri.encode(locationQuery)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$encodedLocation")).apply {
                setPackage("com.google.android.apps.maps")
            }
            try {
                context.startActivity(intent)
            } catch (_: Exception) {
                // If Google Maps app is not installed, open standard geo/browser
                val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=$encodedLocation"))
                context.startActivity(fallbackIntent)
            }
        } catch (_: Exception) {
            Toast.makeText(context, "Could not open maps", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendEmail(context: Context, emailAddress: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$emailAddress"))
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "Could not open email client", Toast.LENGTH_SHORT).show()
        }
    }

    fun addToCalendar(context: Context, title: String, description: String = "Added from Memora") {
        try {
            val intent = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.Events.TITLE, title)
                putExtra(CalendarContract.Events.DESCRIPTION, description)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "Could not open calendar", Toast.LENGTH_SHORT).show()
        }
    }
}

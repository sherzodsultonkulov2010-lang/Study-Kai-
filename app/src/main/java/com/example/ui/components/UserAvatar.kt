package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

import com.example.ui.theme.InstagramBrush

data class AvatarPreset(val key: String, val label: String, val start: Color, val end: Color)

val AvatarPresets = listOf(
    AvatarPreset("avatar_teal", "Sunset", Color(0xFFF77737), Color(0xFFE1306C)),
    AvatarPreset("avatar_indigo", "Purple", Color(0xFF833AB4), Color(0xFFC13584)),
    AvatarPreset("avatar_amber", "Gold", Color(0xFFFCAF45), Color(0xFFF77737)),
    AvatarPreset("avatar_rose", "Rose", Color(0xFFE1306C), Color(0xFFFD1D1D)),
    AvatarPreset("avatar_violet", "Magenta", Color(0xFFC13584), Color(0xFF5851DB)),
    AvatarPreset("avatar_emerald", "Royal", Color(0xFF405DE6), Color(0xFF833AB4))
)

@Composable
fun UserAvatar(
    fullName: String,
    avatarKey: String,
    avatarUri: String? = null,
    size: Dp = 48.dp,
    modifier: Modifier = Modifier
) {
    val preset = AvatarPresets.firstOrNull { it.key == avatarKey } ?: AvatarPresets.first()
    val initials = fullName
        .trim()
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifEmpty { "SK" }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .border(2.2.dp, InstagramBrush, CircleShape)
            .padding(2.5.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(preset.start, preset.end))),
        contentAlignment = Alignment.Center
    ) {
        if (!avatarUri.isNullOrBlank()) {
            AsyncImage(
                model = avatarUri,
                contentDescription = "$fullName profile photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
            )
        } else {
            Text(
                text = initials,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = (size.value * 0.34f).sp
            )
        }
    }
}

@Composable
fun RoleBadge(role: String, studentLabel: String, teacherLabel: String) {
    val isTeacher = role == "TEACHER"
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isTeacher) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primaryContainer
    ) {
        Text(
            text = if (isTeacher) teacherLabel else studentLabel,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = if (isTeacher) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun VerifiedBadge(
    modifier: Modifier = Modifier,
    size: Dp = 18.dp,
    contentDescription: String = "Verified"
) {
    Icon(
        imageVector = Icons.Filled.Verified,
        contentDescription = contentDescription,
        tint = Color(0xFF0095F6),
        modifier = modifier.size(size)
    )
}

@Composable
fun ProfessionBadge(
    profession: String,
    modifier: Modifier = Modifier
) {
    if (profession.isBlank()) return
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.tertiaryContainer,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.WorkOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = profession,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}


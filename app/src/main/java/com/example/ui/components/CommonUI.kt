package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BloodGroup
import com.example.model.VerificationStatus
import com.example.ui.theme.BloodRed600
import com.example.ui.theme.BloodRed800
import com.example.ui.theme.BloodRedContainer
import com.example.ui.theme.PendingAmber
import com.example.ui.theme.PendingAmberContainer
import com.example.ui.theme.PureWhite
import com.example.ui.theme.RejectedRed
import com.example.ui.theme.RejectedRedContainer
import com.example.ui.theme.SuspendedGray
import com.example.ui.theme.SuspendedGrayContainer
import com.example.ui.theme.VerifiedGreen
import com.example.ui.theme.VerifiedGreenContainer

@Composable
fun BloodGroupBadge(
  bloodGroup: BloodGroup,
  modifier: Modifier = Modifier,
  size: Dp = 48.dp,
  fontSize: Int = 16
) {
  Box(
    modifier = modifier
      .size(size)
      .clip(RoundedCornerShape(12.dp))
      .background(BloodRed800)
      .border(1.5.dp, BloodRedContainer, RoundedCornerShape(12.dp)),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = bloodGroup.label,
      color = PureWhite,
      fontWeight = FontWeight.ExtraBold,
      fontSize = fontSize.sp
    )
  }
}

@Composable
fun StatusBadge(
  status: VerificationStatus,
  isBengali: Boolean,
  modifier: Modifier = Modifier
) {
  val (bgColor, textColor, icon) = when (status) {
    VerificationStatus.VERIFIED -> Triple(VerifiedGreenContainer, VerifiedGreen, Icons.Default.CheckCircle)
    VerificationStatus.PENDING -> Triple(PendingAmberContainer, PendingAmber, Icons.Default.HourglassTop)
    VerificationStatus.REJECTED -> Triple(RejectedRedContainer, RejectedRed, Icons.Default.Warning)
    VerificationStatus.SUSPENDED -> Triple(SuspendedGrayContainer, SuspendedGray, Icons.Default.Lock)
  }

  Row(
    modifier = modifier
      .clip(RoundedCornerShape(50))
      .background(bgColor)
      .padding(horizontal = 8.dp, vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = textColor,
      modifier = Modifier.size(14.dp)
    )
    Spacer(modifier = Modifier.width(4.dp))
    Text(
      text = status.displayName(isBengali),
      color = textColor,
      fontSize = 11.sp,
      fontWeight = FontWeight.SemiBold
    )
  }
}

@Composable
fun AvailabilityIndicator(
  isReady: Boolean,
  text: String,
  modifier: Modifier = Modifier
) {
  val (dotColor, bgColor, textColor) = if (isReady) {
    Triple(VerifiedGreen, VerifiedGreenContainer, VerifiedGreen)
  } else {
    Triple(Color(0xFFE65100), Color(0xFFFFF3E0), Color(0xFFBF360C))
  }

  Row(
    modifier = modifier
      .clip(RoundedCornerShape(20.dp))
      .background(bgColor)
      .padding(horizontal = 8.dp, vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(8.dp)
        .clip(CircleShape)
        .background(dotColor)
    )
    Spacer(modifier = Modifier.width(6.dp))
    Text(
      text = text,
      color = textColor,
      fontSize = 11.sp,
      fontWeight = FontWeight.Medium
    )
  }
}

object PhoneIntentHelper {
  fun dialPhone(context: Context, phoneNumber: String) {
    try {
      val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$phoneNumber")
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
      }
      context.startActivity(intent)
    } catch (e: Exception) {
      Toast.makeText(context, "Cannot open dialer: $phoneNumber", Toast.LENGTH_SHORT).show()
    }
  }

  fun sendSms(context: Context, phoneNumber: String, message: String = "") {
    try {
      val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("smsto:$phoneNumber")
        putExtra("sms_body", message)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
      }
      context.startActivity(intent)
    } catch (e: Exception) {
      Toast.makeText(context, "Cannot open SMS app: $phoneNumber", Toast.LENGTH_SHORT).show()
    }
  }
}

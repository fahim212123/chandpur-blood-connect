package com.example.model

import java.util.Calendar

enum class BloodGroup(val label: String) {
  A_POS("A+"),
  A_NEG("A-"),
  B_POS("B+"),
  B_NEG("B-"),
  AB_POS("AB+"),
  AB_NEG("AB-"),
  O_POS("O+"),
  O_NEG("O-");

  companion object {
    fun fromLabel(label: String): BloodGroup? = entries.find { it.label.equals(label.trim(), ignoreCase = true) }
  }
}

enum class Upazila(val enName: String, val bnName: String) {
  CHANDPUR_SADAR("Chandpur Sadar", "চাঁদপুর সদর"),
  FARIDGANJ("Faridganj", "ফরিদগঞ্জ");

  fun displayName(isBengali: Boolean): String = if (isBengali) bnName else enName
}

enum class VerificationStatus(val enName: String, val bnName: String) {
  PENDING("Pending", "অপেক্ষমান"),
  VERIFIED("Verified", "যাচাইকৃত"),
  REJECTED("Rejected", "বাতিল"),
  SUSPENDED("Suspended", "স্থগিত");

  fun displayName(isBengali: Boolean): String = if (isBengali) bnName else enName
}

data class DonorDate(
  val year: Int,
  val month: Int, // 1 to 12
  val day: Int
) : Comparable<DonorDate> {

  fun toFormattedString(): String {
    val monthNames = arrayOf(
      "Jan", "Feb", "Mar", "Apr", "May", "Jun",
      "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )
    val safeMonth = month.coerceIn(1, 12)
    return "$day ${monthNames[safeMonth - 1]}, $year"
  }

  fun toFormattedStringBn(): String {
    val bnMonths = arrayOf(
      "জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন",
      "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর"
    )
    val safeMonth = month.coerceIn(1, 12)
    val bnDay = toBengaliDigits(day.toString())
    val bnYear = toBengaliDigits(year.toString())
    return "$bnDay ${bnMonths[safeMonth - 1]}, $bnYear"
  }

  fun toEpochDays(): Long {
    val cal = Calendar.getInstance()
    cal.set(year, month - 1, day, 0, 0, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis / (1000 * 60 * 60 * 24)
  }

  fun daysSince(today: DonorDate): Long {
    val diffDays = today.toEpochDays() - this.toEpochDays()
    return diffDays.coerceAtLeast(0)
  }

  fun daysRemaining(today: DonorDate): Long {
    val elapsed = daysSince(today)
    return (90 - elapsed).coerceAtLeast(0)
  }

  override fun compareTo(other: DonorDate): Int {
    if (this.year != other.year) return this.year.compareTo(other.year)
    if (this.month != other.month) return this.month.compareTo(other.month)
    return this.day.compareTo(other.day)
  }

  companion object {
    fun today(): DonorDate {
      val cal = Calendar.getInstance()
      return DonorDate(
        year = cal.get(Calendar.YEAR),
        month = cal.get(Calendar.MONTH) + 1,
        day = cal.get(Calendar.DAY_OF_MONTH)
      )
    }

    fun daysAgo(days: Int): DonorDate {
      val cal = Calendar.getInstance()
      cal.add(Calendar.DAY_OF_YEAR, -days)
      return DonorDate(
        year = cal.get(Calendar.YEAR),
        month = cal.get(Calendar.MONTH) + 1,
        day = cal.get(Calendar.DAY_OF_MONTH)
      )
    }

    private fun toBengaliDigits(str: String): String {
      val bengaliDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
      return str.map { ch ->
        if (ch in '0'..'9') bengaliDigits[ch - '0'] else ch
      }.joinToString("")
    }
  }
}

data class Donor(
  val id: String,
  val name: String,
  val nameBn: String? = null,
  val phone: String,
  val bloodGroup: BloodGroup,
  val upazila: Upazila,
  val area: String,
  val areaBn: String? = null,
  val isFirstTimeDonor: Boolean = false,
  val lastDonationDate: DonorDate? = null,
  val totalDonations: Int = 0,
  val verificationStatus: VerificationStatus = VerificationStatus.VERIFIED,
  val isAvailableManually: Boolean = true,
  val registrationDate: DonorDate = DonorDate.today(),
  val emergencyContact: String? = null,
  val notes: String? = null,
  val lastDonationLocation: String? = null
) {
  fun displayName(isBengali: Boolean): String =
    if (isBengali && !nameBn.isNullOrBlank()) nameBn else name

  fun displayArea(isBengali: Boolean): String =
    if (isBengali && !areaBn.isNullOrBlank()) areaBn else area

  fun isEligibleByInterval(today: DonorDate = DonorDate.today()): Boolean {
    if (isFirstTimeDonor || lastDonationDate == null) return true
    return lastDonationDate.daysSince(today) >= 90
  }

  fun daysUntilEligible(today: DonorDate = DonorDate.today()): Long {
    if (isFirstTimeDonor || lastDonationDate == null) return 0
    return lastDonationDate.daysRemaining(today)
  }

  fun isAvailableNow(today: DonorDate = DonorDate.today()): Boolean {
    return isAvailableManually &&
        verificationStatus == VerificationStatus.VERIFIED &&
        isEligibleByInterval(today)
  }
}

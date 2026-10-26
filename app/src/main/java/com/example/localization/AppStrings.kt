package com.example.localization

enum class AppLanguage(val code: String, val label: String) {
  BN("bn", "বাংলা"),
  EN("en", "English")
}

object AppStrings {

  fun appName(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "LifeLink CP"
    AppLanguage.EN -> "LifeLink CP"
  }

  fun appTagline(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "One Drop. One Life. One Community."
    AppLanguage.EN -> "One Drop. One Life. One Community."
  }

  fun appSubtitle(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "চাঁদপুর ও ফরিদগঞ্জ • One Drop. One Life. One Community."
    AppLanguage.EN -> "Chandpur & Faridganj • One Drop. One Life. One Community."
  }

  fun demoBanner(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "ডেমো প্রিভিউ • LifeLink CP • চাঁদপুর ও ফরিদগঞ্জ"
    AppLanguage.EN -> "Demo Preview Data • LifeLink CP • Chandpur & Faridganj"
  }

  // Navigation
  fun tabDonors(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "রক্তদাতা"
    AppLanguage.EN -> "Donors"
  }

  fun tabRegister(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "নিবন্ধন"
    AppLanguage.EN -> "Register"
  }

  fun tabAdmin(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "অ্যাডমিন"
    AppLanguage.EN -> "Admin"
  }

  fun tabLogin(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "লগইন"
    AppLanguage.EN -> "Login"
  }

  fun tabAccount(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "অ্যাকাউন্ট"
    AppLanguage.EN -> "Account"
  }

  // Filters & Search
  fun searchHint(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "নাম, ফোন বা এলাকা খুঁজুন..."
    AppLanguage.EN -> "Search name, phone or area..."
  }

  fun allGroups(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "সকল গ্রুপ"
    AppLanguage.EN -> "All Groups"
  }

  fun allLocations(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "সব এলাকা"
    AppLanguage.EN -> "All Locations"
  }

  fun filterAvailableOnly(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "শুধু প্রস্তুত রক্তদাতা"
    AppLanguage.EN -> "Available Only"
  }

  fun donorsFound(count: Int, lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "${toBengaliDigits(count.toString())} জন রক্তদাতা পাওয়া গেছে"
    AppLanguage.EN -> "$count Donors found"
  }

  // Status & Availability
  fun availableNow(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "রক্তদানে প্রস্তুত"
    AppLanguage.EN -> "Ready to Donate"
  }

  fun eligibleInDays(days: Long, lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "${toBengaliDigits(days.toString())} দিন পর উপযুক্ত"
    AppLanguage.EN -> "Eligible in $days days"
  }

  fun firstTimeDonor(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "প্রথমবার রক্তদাতা"
    AppLanguage.EN -> "First-Time Donor"
  }

  fun lastDonated(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "সর্বশেষ রক্তদান"
    AppLanguage.EN -> "Last Donated"
  }

  fun neverDonated(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "এখনো রক্ত দেননি (নতুন)"
    AppLanguage.EN -> "Never donated yet (New)"
  }

  fun totalDonationsCount(count: Int, lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "${toBengaliDigits(count.toString())} বার রক্তদান"
    AppLanguage.EN -> "$count times donated"
  }

  // Actions
  fun callAction(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "কল করুন"
    AppLanguage.EN -> "Call"
  }

  fun smsAction(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "মেসেজ"
    AppLanguage.EN -> "SMS"
  }

  fun viewProfile(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "প্রোফাইল দেখুন"
    AppLanguage.EN -> "View Profile"
  }

  fun updateDonationDate(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "রক্তদানের তারিখ আপডেট"
    AppLanguage.EN -> "Update Donation Date"
  }

  // Registration Form
  fun regTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "রক্তদাতা হিসেবে নিবন্ধন করুন"
    AppLanguage.EN -> "Register as a Blood Donor"
  }

  fun regSubtitle(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "আপনার এক ব্যাগ রক্ত চাঁদপুর ও ফরিদগঞ্জের একটি জীবন বাঁচাতে পারে।"
    AppLanguage.EN -> "Your blood can save a life across Chandpur & Faridganj."
  }

  fun fullName(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "পূর্ণ নাম"
    AppLanguage.EN -> "Full Name"
  }

  fun phoneNumber(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "মোবাইল নম্বর (১১ ডিজিট)"
    AppLanguage.EN -> "Phone Number (11 digits)"
  }

  fun bloodGroupLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "রক্তের গ্রুপ নির্বাচন করুন"
    AppLanguage.EN -> "Select Blood Group"
  }

  fun upazilaLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "উপজেলা"
    AppLanguage.EN -> "Upazila"
  }

  fun areaLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "ইউনিয়ন / এলাকা / গ্রাম"
    AppLanguage.EN -> "Union / Area / Village"
  }

  fun firstTimeCheckbox(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "আমি প্রথমবার রক্ত দিতে চাই"
    AppLanguage.EN -> "I am a first-time donor"
  }

  fun lastDonationDateLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "সর্বশেষ রক্তদানের তারিখ"
    AppLanguage.EN -> "Last Donation Date"
  }

  fun emergencyContactLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "জরুরি বিকল্প নম্বর (ঐচ্ছিক)"
    AppLanguage.EN -> "Emergency / Alternate Phone (Optional)"
  }

  fun agreementText(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "আমি নিশ্চিত করছি যে আমার তথ্য সঠিক এবং আমি রক্তদানে শারীরিক ও মানসিকভাবে প্রস্তুত।"
    AppLanguage.EN -> "I confirm that my details are accurate and I am medically fit to donate blood."
  }

  fun submitRegistration(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "নিবন্ধন সম্পন্ন করুন"
    AppLanguage.EN -> "Complete Registration"
  }

  fun registrationSuccess(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "অভিনন্দন! রক্তদাতা হিসেবে আপনার তথ্য সফলভাবে নিবন্ধিত হয়েছে।"
    AppLanguage.EN -> "Congratulations! Your donor registration was submitted successfully."
  }

  // Update Donation Date Dialog
  fun updateDateTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "সর্বশেষ রক্তদানের তথ্য আপডেট"
    AppLanguage.EN -> "Update Last Blood Donation"
  }

  fun updateDateDesc(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "নতুন তারিখ দেওয়ার পর ৯০ দিন হিসাব করে আপনার পরবর্তী উপযুক্ততা নির্ধারিত হবে।"
    AppLanguage.EN -> "Your next eligibility will be recalculated automatically (90-day rest interval)."
  }

  fun donationDateOption(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "রক্তদানের সময়কাল"
    AppLanguage.EN -> "Donation Timeline"
  }

  fun donationHospitalLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "রক্তদানের স্থান / হাসপাতাল"
    AppLanguage.EN -> "Donation Place / Hospital"
  }

  fun saveUpdate(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "তথ্য সংরক্ষণ করুন"
    AppLanguage.EN -> "Save Update"
  }

  fun cancel(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "বাতিল"
    AppLanguage.EN -> "Cancel"
  }

  fun close(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "বন্ধ করুন"
    AppLanguage.EN -> "Close"
  }

  // Admin Dashboard
  fun adminTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "অ্যাডমিন ড্যাশবোর্ড"
    AppLanguage.EN -> "Admin Dashboard"
  }

  fun adminSubtitle(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "রক্তদাতা যাচাই ও তথ্য নিয়ন্ত্রণ"
    AppLanguage.EN -> "Donor Verification & Moderation"
  }

  fun approveAction(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "অনুমোদন করুন"
    AppLanguage.EN -> "Approve"
  }

  fun rejectAction(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "বাতিল করুন"
    AppLanguage.EN -> "Reject"
  }

  fun suspendAction(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "স্থগিত করুন"
    AppLanguage.EN -> "Suspend"
  }

  fun reactivateAction(lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "সক্রিয় করুন"
    AppLanguage.EN -> "Reactivate"
  }

  fun noDonorsInStatus(status: String, lang: AppLanguage) = when (lang) {
    AppLanguage.BN -> "এই মুহূর্তে $status তালিকায় কোনো রক্তদাতা নেই।"
    AppLanguage.EN -> "No donors currently in $status status."
  }

  fun toBengaliDigits(str: String): String {
    val bengaliDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
    return str.map { ch ->
      if (ch in '0'..'9') bengaliDigits[ch - '0'] else ch
    }.joinToString("")
  }
}

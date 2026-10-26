package com.example.data

import com.example.model.BloodGroup
import com.example.model.Donor
import com.example.model.DonorDate
import com.example.model.Upazila
import com.example.model.VerificationStatus

object SampleDonors {

  fun getInitialDonors(): List<Donor> = listOf(
    // Chandpur Sadar - Verified & Available
    Donor(
      id = "donor-1",
      name = "Tanvir Hossain",
      nameBn = "তানভীর হোসেন",
      phone = "01712345678",
      bloodGroup = BloodGroup.O_POS,
      upazila = Upazila.CHANDPUR_SADAR,
      area = "Puran Bazar, Chandpur",
      areaBn = "পুরাণ বাজার, চাঁদপুর",
      isFirstTimeDonor = false,
      lastDonationDate = DonorDate.daysAgo(110),
      totalDonations = 7,
      verificationStatus = VerificationStatus.VERIFIED,
      isAvailableManually = true,
      lastDonationLocation = "Chandpur 250 Bed General Hospital",
      notes = "Regular volunteer at Red Crescent Chandpur"
    ),
    Donor(
      id = "donor-2",
      name = "Farhana Akter",
      nameBn = "ফারহানা আক্তার",
      phone = "01823456789",
      bloodGroup = BloodGroup.A_POS,
      upazila = Upazila.CHANDPUR_SADAR,
      area = "Natun Bazar, Hospital Road",
      areaBn = "নতুন বাজার, হাসপাতাল রোড",
      isFirstTimeDonor = false,
      lastDonationDate = DonorDate.daysAgo(98),
      totalDonations = 4,
      verificationStatus = VerificationStatus.VERIFIED,
      isAvailableManually = true,
      lastDonationLocation = "Red Crescent Blood Bank, Chandpur"
    ),
    Donor(
      id = "donor-3",
      name = "Md. Kamrul Islam",
      nameBn = "মোঃ কামরুল ইসলাম",
      phone = "01934567890",
      bloodGroup = BloodGroup.B_POS,
      upazila = Upazila.CHANDPUR_SADAR,
      area = "Baburhat, Chandpur Sadar",
      areaBn = "বাবুরহাট, চাঁদপুর সদর",
      isFirstTimeDonor = true,
      lastDonationDate = null,
      totalDonations = 0,
      verificationStatus = VerificationStatus.VERIFIED,
      isAvailableManually = true,
      notes = "Enthusiastic first-time donor, verified student ID"
    ),
    Donor(
      id = "donor-4",
      name = "Rashedul Karim",
      nameBn = "রাশেদুল করিম",
      phone = "01645678901",
      bloodGroup = BloodGroup.AB_POS,
      upazila = Upazila.CHANDPUR_SADAR,
      area = "Bishnupur, Chandpur",
      areaBn = "বিষ্ণুপুর, চাঁদপুর",
      isFirstTimeDonor = false,
      lastDonationDate = DonorDate.daysAgo(35), // Not eligible yet (needs ~55 more days)
      totalDonations = 3,
      verificationStatus = VerificationStatus.VERIFIED,
      isAvailableManually = true,
      lastDonationLocation = "Chandpur Sadar Hospital"
    ),
    Donor(
      id = "donor-5",
      name = "Nusrat Jahan",
      nameBn = "নুসরাত জাহান",
      phone = "01756789012",
      bloodGroup = BloodGroup.O_NEG, // Rare group!
      upazila = Upazila.CHANDPUR_SADAR,
      area = "Bagadi, Chandpur Sadar",
      areaBn = "বাগাদী, চাঁদপুর সদর",
      isFirstTimeDonor = false,
      lastDonationDate = DonorDate.daysAgo(140),
      totalDonations = 6,
      verificationStatus = VerificationStatus.VERIFIED,
      isAvailableManually = true,
      notes = "Rare O- donor. Dedicated for emergency Thalassemia patients"
    ),

    // Faridganj - Verified & Available
    Donor(
      id = "donor-6",
      name = "Arifur Rahman",
      nameBn = "আরিফুর রহমান",
      phone = "01867890123",
      bloodGroup = BloodGroup.B_POS,
      upazila = Upazila.FARIDGANJ,
      area = "Faridganj Bazar, Sadar",
      areaBn = "ফরিদগঞ্জ বাজার, সদর",
      isFirstTimeDonor = false,
      lastDonationDate = DonorDate.daysAgo(105),
      totalDonations = 5,
      verificationStatus = VerificationStatus.VERIFIED,
      isAvailableManually = true,
      lastDonationLocation = "Faridganj Upazila Health Complex"
    ),
    Donor(
      id = "donor-7",
      name = "Tahmina Begum",
      nameBn = "তাহমিনা বেগম",
      phone = "01978901234",
      bloodGroup = BloodGroup.A_NEG, // Rare group!
      upazila = Upazila.FARIDGANJ,
      area = "Rupsa North, Faridganj",
      areaBn = "রূপসা উত্তর, ফরিদগঞ্জ",
      isFirstTimeDonor = true,
      lastDonationDate = null,
      totalDonations = 0,
      verificationStatus = VerificationStatus.VERIFIED,
      isAvailableManually = true,
      notes = "Ready for urgent call near Rupsa"
    ),
    Donor(
      id = "donor-8",
      name = "Sajid Ahmed",
      nameBn = "সাজিদ আহমেদ",
      phone = "01789012345",
      bloodGroup = BloodGroup.AB_NEG, // Very rare
      upazila = Upazila.FARIDGANJ,
      area = "Gupti, Faridganj",
      areaBn = "গুপ্তি, ফরিদগঞ্জ",
      isFirstTimeDonor = false,
      lastDonationDate = DonorDate.daysAgo(125),
      totalDonations = 8,
      verificationStatus = VerificationStatus.VERIFIED,
      isAvailableManually = true,
      lastDonationLocation = "Faridganj Health Complex"
    ),
    Donor(
      id = "donor-9",
      name = "Mehedi Hasan",
      nameBn = "মেহেদী হাসান",
      phone = "01512345678",
      bloodGroup = BloodGroup.O_POS,
      upazila = Upazila.FARIDGANJ,
      area = "Paikpara, Faridganj",
      areaBn = "পাইকপাড়া, ফরিদগঞ্জ",
      isFirstTimeDonor = false,
      lastDonationDate = DonorDate.daysAgo(20), // Not eligible yet
      totalDonations = 2,
      verificationStatus = VerificationStatus.VERIFIED,
      isAvailableManually = true,
      lastDonationLocation = "Chandpur Sadar Hospital"
    ),
    Donor(
      id = "donor-10",
      name = "Mahfuzur Rahman",
      nameBn = "মাহফুজুর রহমান",
      phone = "01323456789",
      bloodGroup = BloodGroup.B_NEG,
      upazila = Upazila.FARIDGANJ,
      area = "Balithuba, Faridganj",
      areaBn = "বালিথুবা, ফরিদগঞ্জ",
      isFirstTimeDonor = false,
      lastDonationDate = DonorDate.daysAgo(95),
      totalDonations = 9,
      verificationStatus = VerificationStatus.VERIFIED,
      isAvailableManually = true,
      lastDonationLocation = "Faridganj Upazila Health Complex"
    ),

    // Pending Donors (for Admin approval dashboard)
    Donor(
      id = "donor-11",
      name = "Shakil Mahmud",
      nameBn = "শাকিল মাহমুদ",
      phone = "01734567891",
      bloodGroup = BloodGroup.A_POS,
      upazila = Upazila.CHANDPUR_SADAR,
      area = "Kalyanpur, Chandpur",
      areaBn = "কল্যাণপুর, চাঁদপুর",
      isFirstTimeDonor = true,
      lastDonationDate = null,
      totalDonations = 0,
      verificationStatus = VerificationStatus.PENDING,
      isAvailableManually = true,
      notes = "New registrant awaiting NID / Blood test verification"
    ),
    Donor(
      id = "donor-12",
      name = "Sumaiya Khanom",
      nameBn = "সুমাইয়া খানম",
      phone = "01845678912",
      bloodGroup = BloodGroup.O_POS,
      upazila = Upazila.FARIDGANJ,
      area = "Subidpur, Faridganj",
      areaBn = "সুবিদপুর, ফরিদগঞ্জ",
      isFirstTimeDonor = false,
      lastDonationDate = DonorDate.daysAgo(100),
      totalDonations = 1,
      verificationStatus = VerificationStatus.PENDING,
      isAvailableManually = true,
      notes = "Recently moved to Faridganj. Submitted blood donor card."
    ),
    Donor(
      id = "donor-13",
      name = "Zahid Hasan",
      nameBn = "জাহিদ হাসান",
      phone = "01956789123",
      bloodGroup = BloodGroup.B_POS,
      upazila = Upazila.CHANDPUR_SADAR,
      area = "Ashikati, Chandpur Sadar",
      areaBn = "আশিকাটি, চাঁদপুর সদর",
      isFirstTimeDonor = true,
      lastDonationDate = null,
      totalDonations = 0,
      verificationStatus = VerificationStatus.PENDING,
      isAvailableManually = true,
      notes = "College student donor group signup"
    ),

    // Rejected & Suspended Donors (for Admin dashboard)
    Donor(
      id = "donor-14",
      name = "Al-Amin Hossain",
      nameBn = "আল-আমিন হোসেন",
      phone = "01667891234",
      bloodGroup = BloodGroup.A_POS,
      upazila = Upazila.CHANDPUR_SADAR,
      area = "Boro Station, Chandpur",
      areaBn = "বড় স্টেশন, চাঁদপুর",
      isFirstTimeDonor = false,
      lastDonationDate = DonorDate.daysAgo(200),
      totalDonations = 1,
      verificationStatus = VerificationStatus.REJECTED,
      isAvailableManually = false,
      notes = "Low hemoglobin level (10.2 g/dL), advised medical rest"
    ),
    Donor(
      id = "donor-15",
      name = "Nazmul Huda",
      nameBn = "নাজমুল হুদা",
      phone = "01778912345",
      bloodGroup = BloodGroup.O_POS,
      upazila = Upazila.FARIDGANJ,
      area = "Gobindapur, Faridganj",
      areaBn = "গোবিন্দপুর, ফরিদগঞ্জ",
      isFirstTimeDonor = false,
      lastDonationDate = DonorDate.daysAgo(75),
      totalDonations = 4,
      verificationStatus = VerificationStatus.SUSPENDED,
      isAvailableManually = false,
      notes = "Under temporary antibiotic course until next month"
    )
  )
}

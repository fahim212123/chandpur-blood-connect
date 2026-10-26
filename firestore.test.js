const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = "demo-no-project";
const ALICE_UID = "alice_donor_123";
const BOB_UID = "bob_user_456";
const ADMIN_UID = "admin_user_789";
const ADMIN_EMAIL = "hossanfahim01@gmail.com";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

// 1. Unauthenticated Security Bounds
test("Unauthenticated: cannot read or write donors", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("donors").get());
  await assertFails(unauthDb.collection("donors").doc("d1").set({ name: "Fake" }));
  await assertFails(unauthDb.collection("admins").get());
});

// 2. Verified Donors in Public Search
test("Public Search: authenticated users can only view VERIFIED donors", async () => {
  // Seed donors with different statuses using admin bypass
  await testEnv.withSecurityRulesDisabled(async (context) => {
    const db = context.firestore();
    await db.collection("donors").doc("donor_verified").set({
      userId: ALICE_UID,
      name: "Alice Hossain",
      bloodGroup: "O+",
      upazila: "Chandpur Sadar",
      area: "Puran Bazar",
      verificationStatus: "VERIFIED",
      totalDonations: 3,
      createdAt: new Date(),
      updatedAt: new Date()
    });

    await db.collection("donors").doc("donor_pending").set({
      userId: "charlie_uid",
      name: "Charlie Pending",
      bloodGroup: "A+",
      upazila: "Faridganj",
      area: "Rupsa",
      verificationStatus: "PENDING",
      totalDonations: 0,
      createdAt: new Date(),
      updatedAt: new Date()
    });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();

  // Bob can read verified donor
  await assertSucceeds(bobDb.collection("donors").doc("donor_verified").get());

  // Bob CANNOT read pending donor (non-owner, non-admin)
  await assertFails(bobDb.collection("donors").doc("donor_pending").get());
});

// 3. Privacy: Private Contact Details Protected
test("Privacy: non-owner cannot read private contact subcollection", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    const db = context.firestore();
    await db.collection("donors").doc(ALICE_UID).collection("private").doc("contact").set({
      userId: ALICE_UID,
      phone: "01712345678",
      emergencyContact: "01812345678",
      notes: "Strictly private notes",
      updatedAt: new Date()
    });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();

  // Bob CANNOT access Alice's private contact
  await assertFails(bobDb.collection("donors").doc(ALICE_UID).collection("private").doc("contact").get());

  // Alice (the owner) CAN access her own private contact
  await assertSucceeds(aliceDb.collection("donors").doc(ALICE_UID).collection("private").doc("contact").get());
});

// 4. Role Isolation: Users cannot assign themselves admin roles
test("Admin Role Isolation: users cannot write to /admins", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("admins").doc(ALICE_UID).set({
    email: "alice@test.com",
    role: "ADMIN",
    assignedAt: new Date()
  }));
});

// 5. Self-Verification Prevention: Users cannot self-verify
test("Self-Approval Prevention: non-admin cannot register with VERIFIED status", async () => {
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();

  // Trying to register directly as VERIFIED fails
  await assertFails(bobDb.collection("donor_registrations").doc("reg_1").set({
    userId: BOB_UID,
    name: "Bob Self Verified",
    phone: "01812345678",
    bloodGroup: "B+",
    upazila: "Faridganj",
    area: "Faridganj Bazar",
    verificationStatus: "VERIFIED", // Unauthorized!
    createdAt: new Date(),
    updatedAt: new Date()
  }));

  // Registering with PENDING succeeds
  await assertSucceeds(bobDb.collection("donor_registrations").doc("reg_2").set({
    userId: BOB_UID,
    name: "Bob Normal",
    phone: "01812345678",
    bloodGroup: "B+",
    upazila: "Faridganj",
    area: "Faridganj Bazar",
    verificationStatus: "PENDING",
    createdAt: new Date(),
    updatedAt: new Date()
  }));
});

// 6. Admin Privileges: Admin can inspect private contact & moderate registrations
test("Admin: can access private contact and moderate registrations", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    const db = context.firestore();
    await db.collection("donors").doc(ALICE_UID).collection("private").doc("contact").set({
      userId: ALICE_UID,
      phone: "01712345678",
      updatedAt: new Date()
    });

    await db.collection("donor_registrations").doc("reg_pending").set({
      userId: BOB_UID,
      name: "Bob Waiting",
      phone: "01812345678",
      bloodGroup: "B+",
      upazila: "Faridganj",
      area: "Faridganj Bazar",
      verificationStatus: "PENDING",
      createdAt: new Date(),
      updatedAt: new Date()
    });
  });

  const adminDb = testEnv.authenticatedContext(ADMIN_UID, {
    email: ADMIN_EMAIL,
    email_verified: true
  }).firestore();

  // Admin can read private contact
  await assertSucceeds(adminDb.collection("donors").doc(ALICE_UID).collection("private").doc("contact").get());

  // Admin can approve registration to VERIFIED
  await assertSucceeds(adminDb.collection("donor_registrations").doc("reg_pending").update({
    verificationStatus: "VERIFIED",
    updatedAt: new Date()
  }));
});

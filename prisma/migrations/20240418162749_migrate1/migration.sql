-- CreateEnum
CREATE TYPE "UserType" AS ENUM ('ADMIN', 'NORMAL_USER');

-- CreateTable
CREATE TABLE "User" (
    "uuid" SERIAL NOT NULL,
    "firstName" TEXT NOT NULL,
    "lastName" TEXT NOT NULL,
    "avatar" TEXT,
    "username" TEXT NOT NULL,
    "password" TEXT NOT NULL,
    "email" TEXT NOT NULL,
    "type" "UserType" NOT NULL,

    CONSTRAINT "User_pkey" PRIMARY KEY ("uuid")
);

-- CreateTable
CREATE TABLE "Trip" (
    "uuid" SERIAL NOT NULL,
    "description" TEXT NOT NULL,
    "name" TEXT NOT NULL,
    "startDate" TIMESTAMP(3) NOT NULL,
    "endDate" TIMESTAMP(3) NOT NULL,
    "rating" INTEGER NOT NULL,

    CONSTRAINT "Trip_pkey" PRIMARY KEY ("uuid")
);

-- CreateTable
CREATE TABLE "Location" (
    "uuid" SERIAL NOT NULL,
    "name" TEXT NOT NULL,
    "description" TEXT NOT NULL,
    "type" INTEGER NOT NULL,
    "rating" INTEGER NOT NULL,
    "latitude" DOUBLE PRECISION NOT NULL,
    "longitude" DOUBLE PRECISION NOT NULL,
    "tripUuid" INTEGER,

    CONSTRAINT "Location_pkey" PRIMARY KEY ("uuid")
);

-- CreateTable
CREATE TABLE "location_types" (
    "uuid" SERIAL NOT NULL,
    "name" TEXT NOT NULL,

    CONSTRAINT "location_types_pkey" PRIMARY KEY ("uuid")
);

-- CreateTable
CREATE TABLE "Photo" (
    "uuid" SERIAL NOT NULL,
    "url" TEXT NOT NULL,
    "locationId" INTEGER NOT NULL,

    CONSTRAINT "Photo_pkey" PRIMARY KEY ("uuid")
);

-- CreateTable
CREATE TABLE "TripLocation" (
    "tripId" INTEGER NOT NULL,
    "locationId" INTEGER NOT NULL,

    CONSTRAINT "TripLocation_pkey" PRIMARY KEY ("tripId","locationId")
);

-- CreateTable
CREATE TABLE "UserTrip" (
    "userId" INTEGER NOT NULL,
    "tripId" INTEGER NOT NULL,

    CONSTRAINT "UserTrip_pkey" PRIMARY KEY ("userId","tripId")
);

-- CreateIndex
CREATE UNIQUE INDEX "location_types_name_key" ON "location_types"("name");

-- AddForeignKey
ALTER TABLE "Location" ADD CONSTRAINT "Location_tripUuid_fkey" FOREIGN KEY ("tripUuid") REFERENCES "Trip"("uuid") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Photo" ADD CONSTRAINT "Photo_locationId_fkey" FOREIGN KEY ("locationId") REFERENCES "Location"("uuid") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "TripLocation" ADD CONSTRAINT "TripLocation_tripId_fkey" FOREIGN KEY ("tripId") REFERENCES "Trip"("uuid") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "TripLocation" ADD CONSTRAINT "TripLocation_locationId_fkey" FOREIGN KEY ("locationId") REFERENCES "Location"("uuid") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "UserTrip" ADD CONSTRAINT "UserTrip_userId_fkey" FOREIGN KEY ("userId") REFERENCES "User"("uuid") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "UserTrip" ADD CONSTRAINT "UserTrip_tripId_fkey" FOREIGN KEY ("tripId") REFERENCES "Trip"("uuid") ON DELETE RESTRICT ON UPDATE CASCADE;

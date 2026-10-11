package com.example.campusfix;

import com.example.campusfix.model.Report;
import com.example.campusfix.util.DateUtils;
import com.example.campusfix.util.PriorityRules;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CampusFixUnitTest {

    @Test
    public void testPriorityRules_HazardFlag() {
        PriorityRules.AssessmentResult result = PriorityRules.evaluatePriority("Furniture", "Dorm Room 101", true, "Broken leg");
        assertEquals("CRITICAL", result.getPriority());
    }

    @Test
    public void testPriorityRules_ElectricalInLab() {
        PriorityRules.AssessmentResult result = PriorityRules.evaluatePriority("Electrical", "Chemistry Lab 304", false, "Sparks from outlet");
        assertEquals("HIGH", result.getPriority());
    }

    @Test
    public void testPriorityRules_RoutineFurniture() {
        PriorityRules.AssessmentResult result = PriorityRules.evaluatePriority("Furniture", "Classroom 10", false, "Chair cushion torn");
        assertEquals("LOW", result.getPriority());
    }

    @Test
    public void testUniqueReportIdGeneration() {
        String reportId1 = DateUtils.generateUniqueReportId();
        String reportId2 = DateUtils.generateUniqueReportId();

        assertNotNull(reportId1);
        assertTrue(reportId1.startsWith("CF-"));
        assertNotEquals(reportId1, reportId2);
    }

    @Test
    public void testReportDemoFlagSegregation() {
        Report userReport = new Report("CF-001", "Title", "Desc", "Plumbing", "Lab 1", "HIGH", "OPEN", null, System.currentTimeMillis(), System.currentTimeMillis(), false, "user@campus.edu", false);
        Report demoReport = new Report("CF-101", "Demo Title", "Demo Desc", "Electrical", "Library", "MEDIUM", "OPEN", null, System.currentTimeMillis(), System.currentTimeMillis(), false, "demo@campus.edu", true);

        assertFalse(userReport.isDemo());
        assertTrue(demoReport.isDemo());
    }

    @Test
    public void testDateUtilsFormatting() {
        long timestamp = 1716200000000L; // May 20, 2024 timestamp
        String formattedDate = DateUtils.formatDate(timestamp);
        assertNotNull(formattedDate);
        assertTrue(formattedDate.contains("2024"));
    }
}

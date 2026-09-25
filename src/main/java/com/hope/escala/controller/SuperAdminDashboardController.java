package com.hope.escala.controller;

 
 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hope.escala.dto.SuperAdminDashboardDTO;
import com.hope.escala.security.annotation.SomenteSuperAdmin;
import com.hope.escala.service.SuperAdminDashboardService;

@RestController
@RequestMapping("/super-admin/dashboard")
public class SuperAdminDashboardController {

    private final SuperAdminDashboardService dashboardService;

    public SuperAdminDashboardController(SuperAdminDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    @SomenteSuperAdmin
    public ResponseEntity<SuperAdminDashboardDTO> obterMetricasGerais() {
        return ResponseEntity.ok(dashboardService.compilarMetricas());
    }
    
    
}

package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;


    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown(){

    }

    @Test
    @DisplayName("Test pour vérifier qu'un ticket est généré à l'entrée d'une voiture sur le parking + Maj des places dispos")
    public void testParkingACar(){
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        int currentSpot = ticketDAO.getTicket("ABCDEF").getParkingSpot().getId();
        int nextSpot = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        assertNotNull(ticketDAO.getTicket("ABCDEF").getInTime());
        assertNotEquals(currentSpot, nextSpot);

        //TODO: check that a ticket is actualy saved in DB and Parking table is updated with availability
    }

    @Test
    @DisplayName("vérifier que le tarif généré et le temps de sortie sont correctement renseignés dans la base de données")
    public void testParkingLotExit() throws Exception {

        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        Thread.sleep(1000);
        parkingService.processExitingVehicle();
        assertNotNull(ticketDAO.getTicket("ABCDEF").getOutTime());
        assertEquals(ticketDAO.getTicket("ABCDEF").getPrice(), 0);

        //TODO: check that the fare generated and out time are populated correctly in the database
    }

}

package InventoryAPPBE.InventoryAPPBE.controllers;

import InventoryAPPBE.InventoryAPPBE.helpers.BaseResponse;
import InventoryAPPBE.InventoryAPPBE.helpers.RequestHelper;
import InventoryAPPBE.InventoryAPPBE.models.Location;
import InventoryAPPBE.InventoryAPPBE.models.User;
import InventoryAPPBE.InventoryAPPBE.modelsDTO.LocationDTO;
import InventoryAPPBE.InventoryAPPBE.repositories.location.LocationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/locations")
public class LocationController extends AbstractController<LocationDTO> {
    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private RequestHelper requestHelper;

    @Override
    @PostMapping("")
    public ResponseEntity<BaseResponse<LocationDTO>> create(@RequestBody LocationDTO locationDTO) {
        BaseResponse<LocationDTO> response = new BaseResponse<>(null, null, null);
        ModelMapper modelMapper = new ModelMapper();
        try {
            User loggedUser = requestHelper.getUserFromContext();
            Location location = modelMapper.map(locationDTO, Location.class);

            location.setUser(loggedUser);
            int latestIncrementId = locationRepository.getLatestIncrementIdByUser(loggedUser);
            location.setIncrementId(latestIncrementId + 1);

            Location savedValue = locationRepository.create(location);

            LocationDTO locationResponse = modelMapper.map(savedValue, LocationDTO.class);
            response = new BaseResponse<>(locationResponse, "success", "success");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatus("error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @GetMapping("")
    public ResponseEntity<BaseResponse<ArrayList<LocationDTO>>> findAll() {
        BaseResponse<ArrayList<LocationDTO>> response = new BaseResponse<>(null, null, null);
        ModelMapper modelMapper = new ModelMapper();
        ArrayList<LocationDTO> locations = new ArrayList<>();
        try {
            User loggedUser = requestHelper.getUserFromContext();
            ArrayList<Location> location = (ArrayList<Location>) locationRepository.getAllLocation(loggedUser);
            for (Location loc : location) {
                LocationDTO locationDTO = modelMapper.map(loc, LocationDTO.class);
                locations.add(locationDTO);
            }
            response.setData(locations);
            response.setMessage("success");
            response.setStatus("success");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatus("failed");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<LocationDTO>> findById(@PathVariable String id) {
        BaseResponse<LocationDTO> response = new BaseResponse<>(null, null, null);
        ModelMapper modelMapper = new ModelMapper();
        try {
            User loggedUser = requestHelper.getUserFromContext();
            int idInteger = Integer.parseInt(id);
            Location location = locationRepository.findById(idInteger, loggedUser);

            LocationDTO locationDTO = modelMapper.map(location, LocationDTO.class);
            response.setData(locationDTO);
            response.setMessage("Success");
            response.setStatus("success");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatus("failed");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (NumberFormatException e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatus("failed");
            return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (Exception e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatus("error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<LocationDTO>> deleteById(@PathVariable String id) {
        BaseResponse<LocationDTO> response = new BaseResponse<>(null, null, null);
        try {
            User loggedUser = requestHelper.getUserFromContext();
            int idInteger = Integer.parseInt(id);
            locationRepository.findById(idInteger, loggedUser);

            locationRepository.deleteLocation(idInteger);
            response.setData(null);
            response.setMessage("product has been deleted");
            response.setStatus("success");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatus("failed");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.setData(null);
            response.setMessage("Location is being used by a product, please delete related product first");
            response.setStatus("failed");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatus("error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<LocationDTO>> updateById(@PathVariable String id, @RequestBody LocationDTO t) {
        BaseResponse<LocationDTO> response = new BaseResponse<>(null, null, null);
        ModelMapper modelMapper = new ModelMapper();
        try {
            User loggedUser = requestHelper.getUserFromContext();
            int idInteger = Integer.parseInt(id);

            Location locationFromDTO = modelMapper.map(t, Location.class);
            Location location = locationRepository.findById(idInteger, loggedUser);

            locationFromDTO.setId(idInteger);
            locationFromDTO.setCreatedAt(location.getCreatedAt());
            locationFromDTO.setUser(location.getUser());
            Location locations = locationRepository.update(locationFromDTO);

            LocationDTO locationDTO = modelMapper.map(locations, LocationDTO.class);
            response.setData(locationDTO);
            response.setMessage("product has been updated");
            response.setStatus("success");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }  catch (EntityNotFoundException e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatus("failed");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatus("failed");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

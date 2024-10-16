package ee.taltech.iti03022024backend.controller;


import ee.taltech.iti03022024backend.dto.CommentDto;
import ee.taltech.iti03022024backend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/camping_routes/comments")
public class CommentController {
    private final CommentService service;

    @PostMapping("/{campingRouteId}")
    public ResponseEntity<CommentDto> createComment(@RequestBody CommentDto dto, @PathVariable long campingRouteId) {
        return service.createComment(dto, campingRouteId);
    }

    @GetMapping("/{campingRouteId}")
    public ResponseEntity<List<CommentDto>> getCommentsByCampingRoute(@PathVariable long campingRouteId) {
        return service.getCommentsByCampingRoute(campingRouteId);
    }
}
